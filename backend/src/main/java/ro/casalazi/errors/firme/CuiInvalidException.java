package ro.casalazi.errors.firme;

public class CuiInvalidException extends RuntimeException {
    public static final String MESAJ = "CUI-ul nu e valid: cifra de control nu se potrivește.";

    public CuiInvalidException() {
        super(MESAJ);
    }
}
