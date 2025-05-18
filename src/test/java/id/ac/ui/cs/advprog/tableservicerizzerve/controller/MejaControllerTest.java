package id.ac.ui.cs.advprog.tableservicerizzerve.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.tableservicerizzerve.dto.*;
import id.ac.ui.cs.advprog.tableservicerizzerve.enums.MejaStatus;
import id.ac.ui.cs.advprog.tableservicerizzerve.model.Meja;
import id.ac.ui.cs.advprog.tableservicerizzerve.service.MejaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MejaControllerTest {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private MejaService mejaService;

    @InjectMocks
    private MejaController mejaController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mejaController).build();
    }

    @Test
    void testCreateMejaSuccess() throws Exception {
        CreateMejaRequest request = new CreateMejaRequest();
        request.setNomorMeja(1);
        request.setStatus(MejaStatus.TERSEDIA.getValue());
        Meja meja = new Meja(1, MejaStatus.TERSEDIA.getValue());
        meja.setId(UUID.randomUUID());
        when(mejaService.createMeja(1, MejaStatus.TERSEDIA.getValue())).thenReturn(meja);

        mockMvc.perform(post("/api/table/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.meja.nomorMeja").value(1))
                .andExpect(jsonPath("$.meja.status").value(MejaStatus.TERSEDIA.getValue()));
    }

    @Test
    void testUpdateMejaSuccess() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateMejaRequest req = new UpdateMejaRequest();
        req.setNomorMeja(5);
        req.setStatus(MejaStatus.TERSEDIA.getValue());
        Meja meja = new Meja(5, MejaStatus.TERSEDIA.getValue());
        meja.setId(id);
        when(mejaService.updateMeja(eq(id), eq(5), eq(MejaStatus.TERSEDIA.getValue()))).thenReturn(meja);

        mockMvc.perform(put("/api/table/" + id + "/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meja.nomorMeja").value(5));
    }

    @Test
    void testDeleteMejaSuccess() throws Exception {
        UUID id = UUID.randomUUID();
        mockMvc.perform(delete("/api/table/" + id + "/delete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Table deleted successfully"));
    }

    @Test
    void testCheckAvailabilityAvailable() throws Exception {
        Meja meja = new Meja(3, MejaStatus.TERSEDIA.getValue());
        meja.setId(UUID.randomUUID());
        when(mejaService.findByNomorMeja(3)).thenReturn(Optional.of(meja));

        mockMvc.perform(get("/api/table/check-availability?tableNumber=3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.message").value("TERSEDIA"));
    }

    @Test
    void testCheckAvailabilityNotAvailable() throws Exception {
        Meja meja = new Meja(4, MejaStatus.TERPAKAI.getValue());
        meja.setId(UUID.randomUUID());
        when(mejaService.findByNomorMeja(4)).thenReturn(Optional.of(meja));

        mockMvc.perform(get("/api/table/check-availability?tableNumber=4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(false))
                .andExpect(jsonPath("$.message").value("TERPAKAI"));
    }

    @Test
    void testCheckAvailabilityTableNotFound() throws Exception {
        when(mejaService.findByNomorMeja(99)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/table/check-availability?tableNumber=99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(false))
                .andExpect(jsonPath("$.message").value("Table not found"));
    }

    @Test
    void testUpdateStatusSuccess() throws Exception {
        Meja meja = new Meja(7, MejaStatus.TERSEDIA.getValue());
        meja.setId(UUID.randomUUID());
        when(mejaService.findByNomorMeja(7)).thenReturn(Optional.of(meja));
        when(mejaService.updateMeja(any(UUID.class), eq(7), eq(MejaStatus.TERPAKAI.getValue())))
                .thenReturn(meja);

        mockMvc.perform(put("/api/table/update-status?tableNumber=7&status=TERPAKAI"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Status updated"))
                .andExpect(jsonPath("$.table.nomorMeja").value(7));
    }

    @Test
    void testUpdateStatusTableNotFound() throws Exception {
        when(mejaService.findByNomorMeja(88)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/table/update-status?tableNumber=88&status=TERPAKAI"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("Table not found"));
    }

    @Test
    void testGetMejaByIdSuccess() throws Exception {
        UUID id = UUID.randomUUID();
        Meja meja = new Meja(12, MejaStatus.TERSEDIA.getValue()); meja.setId(id);
        when(mejaService.findById(id)).thenReturn(Optional.of(meja));

        mockMvc.perform(get("/api/table/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meja.nomorMeja").value(12));
    }

    @Test
    void testGetMejaByIdNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(mejaService.findById(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/table/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.meja").isEmpty());
    }

    @Test
    void testGetMejaByNomorSuccess() throws Exception {
        Meja meja = new Meja(6, MejaStatus.TERSEDIA.getValue());
        meja.setId(UUID.randomUUID());
        when(mejaService.findByNomorMeja(6)).thenReturn(Optional.of(meja));

        mockMvc.perform(get("/api/table/nomor/6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meja.nomorMeja").value(6));
    }

    @Test
    void testGetMejaByNomorNotFound() throws Exception {
        when(mejaService.findByNomorMeja(66)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/table/nomor/66"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.meja").value((Object) null));
    }
}