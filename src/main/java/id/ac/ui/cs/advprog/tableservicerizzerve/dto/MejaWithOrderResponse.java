package id.ac.ui.cs.advprog.tableservicerizzerve.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MejaWithOrderResponse {
    private UUID mejaId;
    private int nomorMeja;
    private String statusMeja;
    private OrderDataForTableDto currentOrder;
}