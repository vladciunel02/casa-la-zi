package ro.casalazi.firme;

import ro.casalazi.util.CuiUtil;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.CreationTimestamp;
import ro.casalazi.cabinete.Cabinet;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "firma", uniqueConstraints = @UniqueConstraint(
        name = "firma_cabinet_id_cui_unique", columnNames = {"cabinet_id", "cui"}))
public class Firma {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cabinet_id", nullable = false)
    private Cabinet cabinet;

    @Column(nullable = false, length = 255)
    private String denumire;

    @Column(nullable = false, length = 20)
    private String cui;

    @Column(name = "nr_reg_com", nullable = false, length = 20)
    private String nrRegCom;

    @Column(nullable = false, length = 255)
    private String adresa;

    @Column(name = "platitor_tva", nullable = false)
    private boolean platitorTva;

    @Column(nullable = false)
    private boolean activ = true;

    @CreationTimestamp
    @Column(name = "creat_la", nullable = false, updatable = false)
    private Instant creatLa;

    protected Firma() {
        // Constructor necesar pentru JPA.
    }

    public Firma(Cabinet cabinet, String denumire, String cui,
                 String nrRegCom, String adresa, boolean platitorTva) {
        this.cabinet = Objects.requireNonNull(cabinet, "Cabinetul este obligatoriu.");
        this.denumire = denumire;
        setCui(cui);
        this.nrRegCom = nrRegCom;
        this.adresa = adresa;
        this.platitorTva = platitorTva;
    }

    public UUID getId() {
        return id;
    }

    public Cabinet getCabinet() {
        return cabinet;
    }

    public String getDenumire() {
        return denumire;
    }

    public void setDenumire(String denumire) {
        this.denumire = denumire;
    }

    public String getCui() {
        return cui;
    }

    public void setCui(String cui) {
        this.cui = CuiUtil.normalizeaza(cui);
    }

    public String getNrRegCom() {
        return nrRegCom;
    }

    public void setNrRegCom(String nrRegCom) {
        this.nrRegCom = nrRegCom;
    }

    public String getAdresa() {
        return adresa;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    public boolean isPlatitorTva() {
        return platitorTva;
    }

    public void setPlatitorTva(boolean platitorTva) {
        this.platitorTva = platitorTva;
    }

    public boolean isActiv() {
        return activ;
    }

    public void setActiv(boolean activ) {
        this.activ = activ;
    }

    public Instant getCreatLa() {
        return creatLa;
    }
}
