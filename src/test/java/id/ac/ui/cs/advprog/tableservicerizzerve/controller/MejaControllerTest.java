package id.ac.ui.cs.advprog.tableservicerizzerve.controller;

import id.ac.ui.cs.advprog.tableservicerizzerve.dto.CreateMejaRequest;
import id.ac.ui.cs.advprog.tableservicerizzerve.dto.GetAllMejaResponse;
import id.ac.ui.cs.advprog.tableservicerizzerve.model.Meja;
import id.ac.ui.cs.advprog.tableservicerizzerve.service.MejaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MejaControllerTest {

    private MejaService mejaService;
    private MejaController mejaController;

    @BeforeEach
    void setUp() {
        mejaService = mock(MejaService.class);
        mejaController = new MejaController(mejaService);
    }

    @Test
    void testCreateMejaSuccess() {
        Meja meja = new Meja(5, "TERSEDIA");
        when(mejaService.createMeja(5, "TERSEDIA")).thenReturn(meja);

        CreateMejaRequest request = new CreateMejaRequest();
        request.setNomorMeja(5);
        request.setStatus("TERSEDIA");

        ResponseEntity<?> response = mejaController.createMeja(request);

        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    void testGetAllMeja_ReturnsListOfMeja() {
        Meja meja1 = new Meja(1, "TERSEDIA");
        Meja meja2 = new Meja(2, "TERSEDIA");

        when(mejaService.findAllMeja()).thenReturn(List.of(meja1, meja2));

        ResponseEntity<?> response = mejaController.getAllMeja();

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());

        GetAllMejaResponse responseBody = (GetAllMejaResponse) response.getBody();
        assertEquals("success", responseBody.getStatus());
        assertEquals(2, responseBody.getData().size());
    }
}