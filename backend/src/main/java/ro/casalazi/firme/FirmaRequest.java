package ro.casalazi.firme;

import ro.casalazi.errors.firme.CuiInvalidException;

import ro.casalazi.errors.firme.DateFirmaInvalideException;

import ro.casalazi.util.CuiUtil;

import java.util.LinkedHashMap;
import java.util.Map;

/** datele primite la creare si editare. boolean permite detectarea tva-ului omis. */
public record FirmaRequest(String cui, String denumire, String nrRegCom,
                           String adresa, Boolean platitorTva) {

    String valideazaSiNormalizeazaCui() {
        Map<String, String> erori = new LinkedHashMap<>();
        String normalizat = cui == null ? null : CuiUtil.normalizeaza(cui);
        if (!CuiUtil.esteValid(normalizat)) {
            erori.put("cui", CuiInvalidException.MESAJ);
        }
        verificaText(erori, "denumire", denumire, "Denumirea", 255);
        verificaText(erori, "nrRegCom", nrRegCom, "Numărul de la Registrul Comerțului", 20);
        verificaText(erori, "adresa", adresa, "Adresa", 255);
        if (platitorTva == null) {
            erori.put("platitorTva", "Statutul TVA este obligatoriu.");
        }
        if (!erori.isEmpty()) {
            throw new DateFirmaInvalideException(erori);
        }
        return normalizat;
    }

    private static void verificaText(Map<String, String> erori, String camp,
                                     String valoare, String eticheta, int maxim) {
        if (valoare == null || valoare.isBlank()) {
            erori.put(camp, eticheta + (camp.equals("nrRegCom") ? " e obligatoriu." : " e obligatorie."));
        } else if (valoare.codePointCount(0, valoare.length()) > maxim) {
            erori.put(camp, eticheta + " poate avea cel mult " + maxim + " de caractere.");
        }
    }
}
