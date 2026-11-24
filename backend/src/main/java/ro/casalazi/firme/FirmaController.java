package ro.casalazi.firme;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}
