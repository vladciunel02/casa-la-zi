package ro.casalazi.firme;

import ro.casalazi.errors.firme.CuiInvalidException;

import ro.casalazi.errors.firme.CuiDuplicatException;

import ro.casalazi.errors.firme.FirmaNegasitaException;

import jakarta.persistence.EntityManager;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ro.casalazi.cabinete.Cabinet;
import ro.casalazi.util.CuiUtil;
import org.springframework.transaction.annotation.Transactional;
import ro.casalazi.utilizatori.UtilizatorCurent;

import java.util.List;
import java.util.UUID;

/** aplica regulile de acces si de salvare pentru firmele cabinetului curent. */
@Service
public class FirmaService {

    private final FirmaRepository firme;
    private final UtilizatorCurent utilizatorCurent;

    private final EntityManager entityManager;

    public FirmaService(FirmaRepository firme, UtilizatorCurent utilizatorCurent,
                        EntityManager entityManager) {
        this.firme = firme;
        this.utilizatorCurent = utilizatorCurent;
        this.entityManager = entityManager;
    }

    @Transactional(readOnly = true)
    public List<FirmaDto> lista() {
        UUID cabinetId = utilizatorCurent.cabinetId();
        return firme.findAllByCabinetIdAndActivTrueOrderByDenumireAsc(cabinetId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public FirmaDto detalii(UUID id) {
        UUID cabinetId = utilizatorCurent.cabinetId();
        Firma firma = firme.findByIdAndCabinetIdAndActivTrue(id, cabinetId)
                .orElseThrow(FirmaNegasitaException::new);
        return toDto(firma);
    }

    @Transactional
    public FirmaDto adauga(FirmaRequest cerere) {
        String cui = cerere.valideazaSiNormalizeazaCui();
        UUID cabinetId = utilizatorCurent.cabinetId();
        verificaUnicitate(cabinetId, cui, null);
        // pentru inserare avem nevoie doar de id-ul cabinetului.
        Cabinet cabinet = entityManager.getReference(Cabinet.class, cabinetId);
        Firma firma = new Firma(cabinet, cerere.denumire().strip(), cui,
                cerere.nrRegCom().strip(), cerere.adresa().strip(), cerere.platitorTva());
        return toDto(salveaza(firma));
    }

    @Transactional
    public FirmaDto editeaza(UUID id, FirmaRequest cerere) {
        UUID cabinetId = utilizatorCurent.cabinetId();
        Firma firma = cautaPentruModificare(id, cabinetId);
        String cui = cerere.valideazaSiNormalizeazaCui();
        verificaUnicitate(cabinetId, cui, id);
        firma.setCui(cui);
        firma.setDenumire(cerere.denumire().strip());
        firma.setNrRegCom(cerere.nrRegCom().strip());
        firma.setAdresa(cerere.adresa().strip());
        firma.setPlatitorTva(cerere.platitorTva());
        return toDto(salveaza(firma));
    }

    @Transactional
    public void arhiveaza(UUID id) {
        // hibernate salveaza schimbarea la finalul tranzactiei si pastreaza firma in istoric.
        cautaPentruModificare(id, utilizatorCurent.cabinetId()).setActiv(false);
    }

    @Transactional(readOnly = true)
    public String verificaCuiPentruAnaf(String cui) {
        String normalizat = CuiUtil.normalizeaza(cui);
        if (!CuiUtil.esteValid(normalizat)) {
            throw new CuiInvalidException();
        }
        verificaUnicitate(utilizatorCurent.cabinetId(), normalizat, null);
        return normalizat;
    }

    private Firma cautaPentruModificare(UUID id, UUID cabinetId) {
        return firme.findForUpdateByIdAndCabinetIdAndActivTrue(id, cabinetId)
                .orElseThrow(FirmaNegasitaException::new);
    }

    private void verificaUnicitate(UUID cabinetId, String cui, UUID idEditat) {
        // la editare, firma poate pastra propriul cui.
        firme.findByCabinetIdAndCui(cabinetId, cui)
                .filter(existenta -> !existenta.getId().equals(idEditat))
                .ifPresent(existenta -> {
                    throw new CuiDuplicatException(cui, !existenta.isActiv());
                });
    }

    private Firma salveaza(Firma firma) {
        try {
            return firme.saveAndFlush(firma);
        } catch (DataIntegrityViolationException exception) {
            // constrangerea din postgresql acopera si salvarile concurente.
            for (Throwable cauza = exception; cauza != null; cauza = cauza.getCause()) {
                if (cauza instanceof ConstraintViolationException incalcare
                        && "firma_cabinet_id_cui_unique".equals(incalcare.getConstraintName())) {
                    throw new CuiDuplicatException(firma.getCui(), false);
                }
            }
            throw exception;
        }
    }

    private FirmaDto toDto(Firma firma) {
        return new FirmaDto(
                firma.getId(),
                firma.getDenumire(),
                firma.getCui(),
                firma.getNrRegCom(),
                firma.getAdresa(),
                firma.isPlatitorTva(),
                firma.getCreatLa());
    }
}
