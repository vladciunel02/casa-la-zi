package ro.casalazi.utilizatori;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/** identitate demonstrativa pana la implementarea autentificarii. */
@Component
public class UtilizatorCurent {

    private final UUID utilizatorId;
    private final UtilizatorRepository utilizatori;

    public UtilizatorCurent(
            @Value("${casalazi.dev.utilizator-curent-id}") UUID utilizatorId,
            UtilizatorRepository utilizatori) {
        this.utilizatorId = utilizatorId;
        this.utilizatori = utilizatori;
    }

    @Transactional(readOnly = true)
    public UUID cabinetId() {
        Utilizator utilizator = utilizatori.findById(utilizatorId)
                .orElseThrow(() -> new IllegalStateException("Utilizatorul curent configurat nu există."));

        if (!utilizator.isActiv()) {
            throw new IllegalStateException("Utilizatorul curent este inactiv.");
        }
        if (utilizator.getRol() != Rol.CONTABIL) {
            throw new IllegalStateException("Utilizatorul curent trebuie să aibă rolul CONTABIL.");
        }
        if (utilizator.getCabinet() == null || utilizator.getCabinet().getId() == null) {
            throw new IllegalStateException("Utilizatorul curent nu are un cabinet valid.");
        }
        return utilizator.getCabinet().getId();
    }
}
