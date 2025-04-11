package id.ac.ui.cs.advprog.tableservicerizzerve.controller;

import id.ac.ui.cs.advprog.tableservicerizzerve.model.Meja;
import id.ac.ui.cs.advprog.tableservicerizzerve.service.MejaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

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
}