package id.ac.ui.cs.advprog.tableservicerizzerve.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MejaCustomerViewDto {
    private int nomorMeja;
    private String statusMeja;
}