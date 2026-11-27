package ro.casalazi.utilizatori;

import ro.casalazi.util.EmailUtil;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import ro.casalazi.cabinete.Cabinet;
import ro.casalazi.firme.Firma;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "utilizator")
public class Utilizator {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "parola_hash", nullable = false, length = 255)
    private String parolaHash;

    @Column(nullable = false, length = 255)
    private String nume;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Rol rol;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cabinet_id")
    private Cabinet cabinet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "firma_id")
    private Firma firma;

    @Column(nullable = false)
    private boolean activ = true;

    @CreationTimestamp
    @Column(name = "creat_la", nullable = false, updatable = false)
    private Instant creatLa;

    protected Utilizator() {
        // constructor necesar pentru jpa.
    }

    public Utilizator(String email, String parolaHash, String nume,
                      Rol rol, Cabinet cabinet, Firma firma) {
        Objects.requireNonNull(rol, "Rolul este obligatoriu.");
        if (rol == Rol.CONTABIL && (cabinet == null || firma != null)) {
            throw new IllegalArgumentException("Contabilul trebuie să aibă cabinet și să nu aibă firmă.");
        }
        if (rol == Rol.CASIER && (firma == null || cabinet != null)) {
            throw new IllegalArgumentException("Casierul trebuie să aibă firmă și să nu aibă cabinet.");
        }
        setEmail(email);
        this.parolaHash = parolaHash;
        this.nume = nume;
        this.rol = rol;
        this.cabinet = cabinet;
        this.firma = firma;
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = EmailUtil.normalizeaza(email);
    }

    public String getParolaHash() {
        return parolaHash;
    }

    // primeste un hash deja calculat; entitatea nu cripteaza si nu verifica parole.
    public void setParolaHash(String parolaHash) {
        this.parolaHash = parolaHash;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public Rol getRol() {
        return rol;
    }

    public Cabinet getCabinet() {
        return cabinet;
    }

    public Firma getFirma() {
        return firma;
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
