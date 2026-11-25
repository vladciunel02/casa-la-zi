package ro.casalazi.anaf;

import java.util.Optional;

public interface RegistruAnaf {
    Optional<AnafFirmaDto> cauta(String cui);
}
