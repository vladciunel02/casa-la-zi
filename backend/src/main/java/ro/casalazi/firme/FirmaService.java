package ro.casalazi.firme;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.casalazi.utilizatori.UtilizatorCurent;

import java.util.List;
import java.util.UUID;

@Service
public class FirmaService {

    private final FirmaRepository firme;
    private final UtilizatorCurent utilizatorCurent;

    public FirmaService(FirmaRepository firme, UtilizatorCurent utilizatorCurent) {
        this.firme = firme;
        this.utilizatorCurent = utilizatorCurent;
    }

    @Transactional(readOnly = true)
    public List<FirmaDto> lista() {
        UUID cabinetId = utilizatorCurent.cabinetId();
        return firme.findAllByCabinetIdAndActivTrueOrderByDenumireAsc(cabinetId)
                .stream()
                .map(this::toDto)
                .toList();
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
