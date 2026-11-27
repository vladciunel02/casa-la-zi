package ro.casalazi.anaf;

public record AnafFirmaDto(String cui, String denumire, String nrRegCom,
                          String adresa, boolean platitorTva, boolean inactivFiscal) {
}
