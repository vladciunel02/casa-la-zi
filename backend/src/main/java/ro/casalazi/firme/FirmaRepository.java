package ro.casalazi.firme;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** spring data jpa construieste interogarile pe baza numelor metodelor. */
public interface FirmaRepository extends JpaRepository<Firma, UUID> {

    List<Firma> findAllByCabinetIdAndActivTrueOrderByDenumireAsc(UUID cabinetId);

    Optional<Firma> findByIdAndCabinetIdAndActivTrue(UUID id, UUID cabinetId);

    // tine randul blocat pana la finalul tranzactiei de editare sau arhivare.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Firma> findForUpdateByIdAndCabinetIdAndActivTrue(UUID id, UUID cabinetId);

    // include si firmele arhivate: cui-ul lor ramane ocupat in cabinet.
    Optional<Firma> findByCabinetIdAndCui(UUID cabinetId, String cui);
}
