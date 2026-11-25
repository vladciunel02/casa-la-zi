package ro.casalazi.errors.firme;

public class CuiDuplicatException extends RuntimeException {

    public CuiDuplicatException(String cui, boolean arhivata) {
        super("Firma cu CUI-ul " + cui + (arhivata
                ? " e arhivată în cabinetul tău."
                : " există deja în cabinetul tău."));
    }
}
