package ro.casalazi.firme;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FirmaRepository extends JpaRepository<Firma, UUID> {

    List<Firma> findAllByCabinetIdAndActivTrueOrderByDenumireAsc(UUID cabinetId);

    Optional<Firma> findByIdAndCabinetIdAndActivTrue(UUID id, UUID cabinetId);

    // Include și firmele arhivate: CUI-ul lor rămâne ocupat în cabinet.
    Optional<Firma> findByCabinetIdAndCui(UUID cabinetId, String cui);
}
