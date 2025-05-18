package id.ac.ui.cs.advprog.tableservicerizzerve.observer;

import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class MejaEvent {

    public enum Type {
        CREATED,
        DELETED,
        UPDATED_NOMOR,
        UPDATED_STATUS
    }

    private Type type;
    private UUID mejaId;
    private Integer nomorMeja;
    private Integer oldNomor;
    private String status;
    private Instant occurredAt;
}