package id.ac.ui.cs.advprog.tableservicerizzerve.service;
import id.ac.ui.cs.advprog.tableservicerizzerve.dto.OrderDetailsEvent;

public interface MejaOrderUpdaterService {
    void updateMejaWithActiveOrderDetails(OrderDetailsEvent orderEvent);
    void clearActiveOrderFromMeja(String tableNumber);
    void handleOrderCreatedForTable(OrderDetailsEvent orderEvent);
}