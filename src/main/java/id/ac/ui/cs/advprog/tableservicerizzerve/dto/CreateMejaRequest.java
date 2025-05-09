package id.ac.ui.cs.advprog.tableservicerizzerve.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
public class CreateMejaRequest {
    @NotBlank(message = "Nomor meja cannot be emoty")
    private int nomorMeja;
    private String status;
}