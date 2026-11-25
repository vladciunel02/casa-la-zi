package ro.casalazi.errors.anaf;

public class AnafFirmaNegasitaException extends RuntimeException {
    public AnafFirmaNegasitaException(String cui) {
        super("ANAF nu are o firmă cu CUI-ul " + cui + ".");
    }
}
