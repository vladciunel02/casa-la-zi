package ro.casalazi.anaf;

import ro.casalazi.errors.anaf.AnafFirmaNegasitaException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ro.casalazi.firme.FirmaService;

@RestController
public class AnafController {
    private final FirmaService firme;
    private final RegistruAnaf registru;

    public AnafController(FirmaService firme, RegistruAnaf registru) {
        this.firme = firme;
        this.registru = registru;
    }

    @GetMapping("/api/anaf/{cui}")
    public AnafFirmaDto cauta(@PathVariable("cui") String cui) {
        // validarea si verificarea duplicatelor preced cererea externa.
        String normalizat = firme.verificaCuiPentruAnaf(cui);
        return registru.cauta(normalizat)
                .orElseThrow(() -> new AnafFirmaNegasitaException(normalizat));
    }
}
