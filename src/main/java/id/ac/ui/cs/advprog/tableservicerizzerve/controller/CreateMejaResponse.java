package id.ac.ui.cs.advprog.tableservicerizzerve.controller;

import id.ac.ui.cs.advprog.tableservicerizzerve.model.Meja;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateMejaResponse {
    private String status;
    private Meja meja;
}