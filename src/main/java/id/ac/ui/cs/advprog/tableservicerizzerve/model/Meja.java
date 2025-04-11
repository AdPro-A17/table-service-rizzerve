package id.ac.ui.cs.advprog.tableservicerizzerve.model;

import id.ac.ui.cs.advprog.tableservicerizzerve.enums.MejaStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Meja {

    private UUID id;
    private int nomorMeja;
    private MejaStatus status;

    public Meja(int nomorMeja, String status) {
        this.id = UUID.randomUUID();
        this.nomorMeja = nomorMeja;
        this.status = MejaStatus.fromString(status);
    }

    public String getStatus() {
        return status.getValue();
    }
}