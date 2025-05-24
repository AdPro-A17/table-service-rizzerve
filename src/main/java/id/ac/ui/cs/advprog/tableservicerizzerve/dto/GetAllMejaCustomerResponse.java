package id.ac.ui.cs.advprog.tableservicerizzerve.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetAllMejaCustomerResponse {
    private String message;
    private List<MejaCustomerViewDto> data;
}