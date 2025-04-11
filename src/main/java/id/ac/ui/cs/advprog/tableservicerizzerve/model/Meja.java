package id.ac.ui.cs.advprog.tableservicerizzerve.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Meja {

    private UUID id;
    private int nomorMeja;
    private String status;

    public Meja(int nomorMeja, String status) {
        this.id = UUID.randomUUID();
        this.nomorMeja = nomorMeja;
        this.status = status != null ? status : "tersedia";
    }
}