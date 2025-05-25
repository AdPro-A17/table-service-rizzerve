package id.ac.ui.cs.advprog.tableservicerizzerve.dto;

import id.ac.ui.cs.advprog.tableservicerizzerve.model.Meja;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TableUpdateResponse {
    private String status;
    private Meja table;
}
