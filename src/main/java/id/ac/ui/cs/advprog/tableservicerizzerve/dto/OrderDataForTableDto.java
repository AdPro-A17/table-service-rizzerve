package id.ac.ui.cs.advprog.tableservicerizzerve.dto;
// ... (isi OrderDataForTableDto.java seperti yang sudah Anda setujui sebelumnya)
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDataForTableDto {
    private UUID orderId;
    private String orderStatus;
    private double totalPrice;
    private List<OrderItemSummaryDto> items;
}