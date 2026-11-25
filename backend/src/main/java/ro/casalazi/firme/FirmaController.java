package ro.casalazi.firme;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

/** expune operatiile pe firme prin http si transmite cererile catre service. */
@RestController
@RequestMapping("/api/firme")
public class FirmaController {

    private final FirmaService service;

    public FirmaController(FirmaService service) {
        this.service = service;
    }

    @GetMapping
    public List<FirmaDto> lista() {
        return service.lista();
    }

    @GetMapping("/{id}")
    public FirmaDto detalii(@PathVariable("id") UUID id) {
        return service.detalii(id);
    }

    @PostMapping
    public ResponseEntity<FirmaDto> adauga(@RequestBody FirmaRequest cerere) {
        FirmaDto firma = service.adauga(cerere);
        // location indica ruta de la care clientul poate citi firma creata.
        return ResponseEntity.created(URI.create("/api/firme/" + firma.id())).body(firma);
    }

    @PutMapping("/{id}")
    public FirmaDto editeaza(@PathVariable("id") UUID id, @RequestBody FirmaRequest cerere) {
        return service.editeaza(id, cerere);
    }

    @PostMapping("/{id}/arhivare")
    public ResponseEntity<Void> arhiveaza(@PathVariable("id") UUID id) {
        service.arhiveaza(id);
        return ResponseEntity.noContent().build();
    }
}
