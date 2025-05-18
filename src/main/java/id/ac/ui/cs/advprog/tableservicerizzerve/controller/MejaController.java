package id.ac.ui.cs.advprog.tableservicerizzerve.controller;

import id.ac.ui.cs.advprog.tableservicerizzerve.dto.*;
import id.ac.ui.cs.advprog.tableservicerizzerve.model.Meja;
import id.ac.ui.cs.advprog.tableservicerizzerve.service.MejaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import id.ac.ui.cs.advprog.tableservicerizzerve.enums.MejaStatus;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

@RestController
@RequestMapping("/api/table")
@RequiredArgsConstructor
public class MejaController {

    private final MejaService mejaService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<CreateMejaResponse> createMeja(@RequestBody CreateMejaRequest request) {
        Meja meja = mejaService.createMeja(request.getNomorMeja(), request.getStatus());
        return ResponseEntity.status(201).body(new CreateMejaResponse("Table created successfully", meja));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<GetAllMejaResponse> getAllMeja() {
        List<Meja> mejaList = mejaService.findAllMeja();
        GetAllMejaResponse response = new GetAllMejaResponse("Retrieved all tables successfully", mejaList);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<GetMejaResponse> getMejaById(@PathVariable UUID id) {
        return mejaService.findById(id)
                .map(m -> ResponseEntity.ok(new GetMejaResponse("Table found", m)))
                .orElseGet(() -> ResponseEntity.status(404).body(new GetMejaResponse("Table not found", null)));
    }

    @GetMapping("/nomor/{nomorMeja}")
    public ResponseEntity<GetMejaResponse> getMejaByNomor(@PathVariable int nomorMeja) {
        return mejaService.findByNomorMeja(nomorMeja)
                .map(m -> ResponseEntity.ok(new GetMejaResponse("Table found", m)))
                .orElseGet(() -> ResponseEntity.status(404).body(new GetMejaResponse("Table not found", null)));
    }

    @PutMapping("/{id}/update")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<UpdateMejaResponse> updateMeja(@PathVariable UUID id, @RequestBody UpdateMejaRequest req){
        Meja meja = mejaService.updateMeja(id, req.getNomorMeja(), req.getStatus());
        return ResponseEntity.ok(new UpdateMejaResponse("Table updated successfully", meja));
    }

    @DeleteMapping("/{id}/delete")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<DeleteMejaResponse> deleteMeja(@PathVariable UUID id){
        mejaService.deleteMeja(id);
        return ResponseEntity.ok(new DeleteMejaResponse("Table deleted successfully"));
    }

    @GetMapping("/check-availability")
    public ResponseEntity<TableAvailabilityResponse> checkAvailability(
            @RequestParam("tableNumber") int tableNumber) {
        Optional<Meja> mejaOpt = mejaService.findByNomorMeja(tableNumber);
        if (mejaOpt.isEmpty()) {
            return ResponseEntity.ok(new TableAvailabilityResponse(false, "Table not found"));
        }
        Meja meja = mejaOpt.get();
        boolean available = meja.getStatus().equalsIgnoreCase("TERSEDIA");
        return ResponseEntity.ok(new TableAvailabilityResponse(available, available ? "TERSEDIA" : "TERPAKAI"));
    }

    @PutMapping("/update-status")
    public ResponseEntity<TableUpdateResponse> updateStatus(
            @RequestParam("tableNumber") int tableNumber,
            @RequestParam("status") String status) {
        Optional<Meja> mejaOpt = mejaService.findByNomorMeja(tableNumber);
        if (mejaOpt.isEmpty()) {
            return ResponseEntity.status(404).body(new TableUpdateResponse("Table not found", null));
        }
        Meja meja = mejaOpt.get();
        meja.setStatus(MejaStatus.fromString(status));
        mejaService.updateMeja(meja.getId(), meja.getNomorMeja(), status);
        return ResponseEntity.ok(new TableUpdateResponse("Status updated", meja));
    }
}