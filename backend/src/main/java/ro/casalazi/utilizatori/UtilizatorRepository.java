package ro.casalazi.utilizatori;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UtilizatorRepository extends JpaRepository<Utilizator, UUID> {
}
