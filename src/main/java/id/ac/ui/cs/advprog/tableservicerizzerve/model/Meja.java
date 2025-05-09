package id.ac.ui.cs.advprog.tableservicerizzerve.model;

import id.ac.ui.cs.advprog.tableservicerizzerve.enums.MejaStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "meja")
@Getter
@Setter
@NoArgsConstructor
public class Meja {

    @Id
    private UUID id;

    @Column(name = "nomor_meja", unique = true, nullable = false)
    private int nomorMeja;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MejaStatus status;

    public Meja(int nomorMeja, String status) {
        this.id = UUID.randomUUID();
        this.nomorMeja = nomorMeja;
        this.status = MejaStatus.fromString(status);
    }

    @Transient
    public String getStatus() {
        return status.getValue();
    }
}