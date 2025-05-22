package id.ac.ui.cs.advprog.tableservicerizzerve.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import id.ac.ui.cs.advprog.tableservicerizzerve.dto.*;
import id.ac.ui.cs.advprog.tableservicerizzerve.enums.MejaStatus;
import id.ac.ui.cs.advprog.tableservicerizzerve.exception.MejaNotFoundException;
import id.ac.ui.cs.advprog.tableservicerizzerve.model.Meja;
import id.ac.ui.cs.advprog.tableservicerizzerve.service.MejaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;

@ExtendWith(MockitoExtension.class)
class MejaControllerTest {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Mock
    private MejaService mejaService;

    @InjectMocks
    private MejaController mejaController;

    @BeforeEach
    void setUp() {
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
                .andExpect(jsonPath("$.status").value("Table created successfully"))
                .andExpect(jsonPath("$.meja.nomorMeja").value(1))
                .andExpect(jsonPath("$.meja.status").value(MejaStatus.TERSEDIA.getValue()));
    }

    @Test
    void testGetAllMejaSuccess() throws Exception {
        Meja meja1 = new Meja(1, MejaStatus.TERSEDIA.getValue());
        meja1.setId(UUID.randomUUID());
        Meja meja2 = new Meja(2, MejaStatus.TERPAKAI.getValue());
        meja2.setId(UUID.randomUUID());
        List<Meja> mejaList = List.of(meja1, meja2);

        when(mejaService.findAllMeja()).thenReturn(mejaList);

        mockMvc.perform(get("/api/table")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Retrieved all tables successfully"))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].nomorMeja").value(1))
                .andExpect(jsonPath("$.data[1].nomorMeja").value(2));
    }

    @Test
    void testGetMejaByIdSuccess() throws Exception {
        UUID id = UUID.randomUUID();
        MejaWithOrderResponse responseDto = MejaWithOrderResponse.builder()
                .mejaId(id).nomorMeja(12).statusMeja(MejaStatus.TERSEDIA.getValue()).currentOrder(null).build();
        when(mejaService.findById(id)).thenReturn(responseDto);

        mockMvc.perform(get("/api/table/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mejaId").value(id.toString()))
                .andExpect(jsonPath("$.nomorMeja").value(12));
    }

    @Test
    void testGetMejaByIdNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(mejaService.findById(id)).thenThrow(new MejaNotFoundException());
        mockMvc.perform(get("/api/table/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetMejaByNomorSuccess() throws Exception {
        Meja meja = new Meja(6, MejaStatus.TERSEDIA.getValue());
        when(mejaService.findByNomorMeja(6)).thenReturn(Optional.of(meja));

        mockMvc.perform(get("/api/table/nomor/6")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Table found"))
                .andExpect(jsonPath("$.meja.nomorMeja").value(6));
    }

    @Test
    void testGetMejaByNomorNotFound() throws Exception {
        when(mejaService.findByNomorMeja(66)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/table/nomor/66")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Table not found"))
                .andExpect(jsonPath("$.meja").value(is(new Object[]{null}[0])));
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
                .andExpect(jsonPath("$.status").value("Table updated successfully"))
                .andExpect(jsonPath("$.meja.nomorMeja").value(5));
    }

    @Test
    void testDeleteMejaSuccess() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(mejaService).deleteMeja(id);
        mockMvc.perform(delete("/api/table/" + id + "/delete")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Table deleted successfully"));
    }

    @Test
    void testCheckAvailabilityAvailable() throws Exception {
        Meja meja = new Meja(3, MejaStatus.TERSEDIA.getValue());
        when(mejaService.findByNomorMeja(3)).thenReturn(Optional.of(meja));
        mockMvc.perform(get("/api/table/check-availability?tableNumber=3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.message").value("TERSEDIA"));
    }

    @Test
    void testCheckAvailabilityNotAvailable() throws Exception {
        Meja meja = new Meja(4, MejaStatus.TERPAKAI.getValue());
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
        int tableNumber = 7;
        String newStatus = MejaStatus.TERPAKAI.getValue();
        UUID mejaId = UUID.randomUUID();
        Meja mejaAwal = new Meja(tableNumber, MejaStatus.TERSEDIA.getValue());
        mejaAwal.setId(mejaId);
        Meja mejaSetelahUpdate = new Meja(tableNumber, newStatus);
        mejaSetelahUpdate.setId(mejaId);

        when(mejaService.findByNomorMeja(tableNumber)).thenReturn(Optional.of(mejaAwal));
        when(mejaService.updateMeja(eq(mejaId), eq(tableNumber), eq(newStatus))).thenReturn(mejaSetelahUpdate);

        mockMvc.perform(put("/api/table/update-status?tableNumber=" + tableNumber + "&status=" + newStatus))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Status updated"))
                .andExpect(jsonPath("$.table.nomorMeja").value(tableNumber))
                .andExpect(jsonPath("$.table.status").value(newStatus));
    }

    @Test
    void testUpdateStatusTableNotFound() throws Exception {
        when(mejaService.findByNomorMeja(88)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/table/update-status?tableNumber=88&status=TERPAKAI"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("Table not found"));
    }
}