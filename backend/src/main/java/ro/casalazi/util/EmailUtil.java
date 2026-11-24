package ro.casalazi.util;

import java.util.Locale;
import java.util.Objects;

public final class EmailUtil {

    private EmailUtil() {
    }

    public static String normalizeaza(String email) {
        return Objects.requireNonNull(email, "Emailul este obligatoriu.")
                .trim().toLowerCase(Locale.ROOT);
    }
}
