package ro.casalazi.errors.anaf;

public class AnafLimitaException extends RuntimeException {

    public AnafLimitaException() {
        super("Căutările în registrul ANAF sunt temporar limitate. Încearcă din nou în câteva secunde.");
    }
}
