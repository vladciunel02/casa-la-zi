package ro.casalazi.cabinete;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "cabinet")
public class Cabinet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String denumire;

    @CreationTimestamp
    @Column(name = "creat_la", nullable = false, updatable = false)
    private Instant creatLa;

    protected Cabinet() {
        // constructor necesar pentru jpa.
    }

    public Cabinet(String denumire) {
        this.denumire = denumire;
    }

    public UUID getId() {
        return id;
    }

    public String getDenumire() {
        return denumire;
    }

    public void setDenumire(String denumire) {
        this.denumire = denumire;
    }

    public Instant getCreatLa() {
        return creatLa;
    }
}
