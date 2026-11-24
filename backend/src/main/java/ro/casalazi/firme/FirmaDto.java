package ro.casalazi.firme;

import java.time.Instant;
import java.util.UUID;

public record FirmaDto(
        UUID id,
        String denumire,
        String cui,
        String nrRegCom,
        String adresa,
        boolean platitorTva,
        Instant creatLa) {
}
