package id.ac.ui.cs.advprog.tableservicerizzerve.dto;

import lombok.Data;

@Data
public class TableAvailabilityResponse {
    private boolean available;
    private String message;

    public TableAvailabilityResponse(boolean available, String message) {
        this.available = available;
        this.message = message;
    }
}