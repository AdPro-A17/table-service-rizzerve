package id.ac.ui.cs.advprog.tableservicerizzerve.dto;

import id.ac.ui.cs.advprog.tableservicerizzerve.model.Meja;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetMejaResponse {
    private String message;
    private Meja meja;
}