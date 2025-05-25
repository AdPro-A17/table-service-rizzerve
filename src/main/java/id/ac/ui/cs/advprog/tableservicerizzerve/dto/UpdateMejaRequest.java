package id.ac.ui.cs.advprog.tableservicerizzerve.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
public class UpdateMejaRequest {
    @NotBlank(message = "Nomor meja cannot be empty")
    private int nomorMeja;
    private String status;
}