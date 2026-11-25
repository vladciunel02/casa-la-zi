package ro.casalazi.util;

import java.util.Objects;

public final class CuiUtil {

    private static final String CHEIE_CONTROL = "753217532";

    private CuiUtil() {
    }

    // normalizarea nu valideaza formatul sau cifra de control.
    public static String normalizeaza(String cui) {
        String rezultat = Objects.requireNonNull(cui, "CUI-ul este obligatoriu.")
                .replaceAll("\\s+", "");
        if (rezultat.regionMatches(true, 0, "RO", 0, 2)) {
            rezultat = rezultat.substring(2);
        }
        return rezultat;
    }

    /** verifica un cui deja normalizat; nu confirma existenta firmei in anaf. */
    public static boolean esteValid(String cui) {
        if (cui == null || !cui.matches("[0-9]{2,10}") || cui.matches("0+")) {
            return false;
        }

        int lungimeFaraControl = cui.length() - 1;
        int offsetCheie = CHEIE_CONTROL.length() - lungimeFaraControl;
        int suma = 0;
        // aliniere la dreapta: cifrele lipsa din stanga ar fi zerouri.
        for (int i = 0; i < lungimeFaraControl; i++) {
            int cifra = cui.charAt(i) - '0';
            int pondere = CHEIE_CONTROL.charAt(offsetCheie + i) - '0';
            suma += cifra * pondere;
        }

        int cifraCalculata = (suma * 10) % 11;
        if (cifraCalculata == 10) {
            cifraCalculata = 0;
        }
        int cifraScrisa = cui.charAt(cui.length() - 1) - '0';
        return cifraCalculata == cifraScrisa;
    }

}
