package ro.casalazi.errors.firme;

import java.util.Map;

public class DateFirmaInvalideException extends RuntimeException {

    private final Map<String, String> erori;

    public DateFirmaInvalideException(Map<String, String> erori) {
        super("Datele firmei nu sunt complete.");
        this.erori = Map.copyOf(erori);
    }

    public Map<String, String> getErori() {
        return erori;
    }
}
