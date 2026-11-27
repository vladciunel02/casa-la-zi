package ro.casalazi.errors.firme;

public class CuiInvalidException extends RuntimeException {
    public static final String MESAJ = "CUI-ul nu e valid.";

    public CuiInvalidException() {
        super(MESAJ);
    }
}
