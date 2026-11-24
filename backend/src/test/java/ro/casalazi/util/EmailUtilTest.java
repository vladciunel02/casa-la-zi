package ro.casalazi.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class EmailUtilTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "ion@gmail.com", "Ion@Gmail.com", " ION@GMAIL.COM ", "\tIon@Gmail.com\n"
    })
    void trimsAndLowercasesEmail(String input) {
        String normalized = EmailUtil.normalizeaza(input);
        assertEquals("ion@gmail.com", normalized);
        assertEquals(normalized, EmailUtil.normalizeaza(normalized));
    }

    @Test
    void normalizationIsIndependentOfDefaultLocale() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            assertEquals("ion@example.com", EmailUtil.normalizeaza("ION@EXAMPLE.COM"));
        } finally {
            Locale.setDefault(original);
        }
    }

    @Test
    void preservesAddressComponentsAndInternalWhitespace() {
        assertEquals("ana.popescu+test@example.com",
                EmailUtil.normalizeaza(" ANA.POPESCU+TEST@EXAMPLE.COM "));
        // Helperul normalizează; validarea adresei se face separat.
        assertEquals("ion @example.com", EmailUtil.normalizeaza("ION @EXAMPLE.COM"));
    }

    @Test
    void emptyEmailRemainsEmptyForSubsequentValidation() {
        assertEquals("", EmailUtil.normalizeaza("  "));
    }

    @Test
    void rejectsNullExplicitly() {
        assertThrows(NullPointerException.class, () -> EmailUtil.normalizeaza(null));
    }
}
