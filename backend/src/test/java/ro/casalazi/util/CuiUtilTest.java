package ro.casalazi.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class CuiUtilTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "18452300", "30117720", "29004418", "15632285", "34772153",
            "22198643", "40125569", "36981008", "27115304", "41982735"
    })
    void acceptsDesignAndMockCuis(String cui) {
        assertTrue(CuiUtil.esteValid(cui));
    }

    @ParameterizedTest
    @ValueSource(strings = {"19", "1234567897"})
    void acceptsValidCuisAtBothLengthLimits(String cui) {
        assertTrue(CuiUtil.esteValid(cui));
    }

    @Test
    void mapsControlRemainderTenToZero() {
        assertTrue(CuiUtil.esteValid("60"));
        assertFalse(CuiUtil.esteValid("61"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
            " ", "1", "12345678901", "12345678", "18452301",
            "RO18452300", "184 52300", "12ab34", "-18452300", "+18452300",
            "18452300.0", "١٨٤٥٢٣٠٠", "00", "0000000000", "18452300\n"
    })
    void rejectsMissingMalformedOrWrongChecksumCuis(String cui) {
        assertFalse(CuiUtil.esteValid(cui));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "18452300", "RO18452300", "ro 18452300", "Ro18452300",
            " RO 18 452 300 ", "\tRO\t18452300\n"
    })
    void normalizesPrefixAndWhitespace(String input) {
        String normalized = CuiUtil.normalizeaza(input);
        assertEquals("18452300", normalized);
        assertTrue(CuiUtil.esteValid(normalized));
        assertEquals(normalized, CuiUtil.normalizeaza(normalized));
    }

    @Test
    void normalizationDoesNotEraseInvalidLettersOrSigns() {
        assertEquals("99-ab", CuiUtil.normalizeaza("RO 99-ab"));
        assertFalse(CuiUtil.esteValid(CuiUtil.normalizeaza("RO 99-ab")));
    }

    @Test
    void normalizationRejectsNullExplicitly() {
        assertThrows(NullPointerException.class, () -> CuiUtil.normalizeaza(null));
    }

    @Test
    void emptyInputStaysEmptyAndIsInvalid() {
        assertEquals("", CuiUtil.normalizeaza("  "));
        assertEquals("", CuiUtil.normalizeaza(" RO "));
        assertFalse(CuiUtil.esteValid(CuiUtil.normalizeaza(" RO ")));
    }
    @ParameterizedTest
    @ValueSource(strings = {"01590082", "ro 01590082"})
    void rejectsLeadingZeroEvenWhenChecksumMatches(String input) {
        assertEquals("01590082", CuiUtil.normalizeaza(input));
        assertFalse(CuiUtil.esteValid(CuiUtil.normalizeaza(input)));
    }
}
