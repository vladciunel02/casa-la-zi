package ro.casalazi.errors.firme;

public class FirmaNegasitaException extends RuntimeException {

    public FirmaNegasitaException() {
        super("Firma nu există.");
    }
}
