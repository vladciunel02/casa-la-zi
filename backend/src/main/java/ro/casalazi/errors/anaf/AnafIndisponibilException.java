package ro.casalazi.errors.anaf;

public class AnafIndisponibilException extends RuntimeException {
    public AnafIndisponibilException(Throwable cauza) {
        super("Registrul ANAF nu răspunde. Încearcă din nou sau completează datele de mână.", cauza);
    }
}
