package id.ac.ui.cs.advprog.tableservicerizzerve.dto;

import id.ac.ui.cs.advprog.tableservicerizzerve.model.Meja;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetAllMejaResponse {
    private String status;
    private List<Meja> data;
}