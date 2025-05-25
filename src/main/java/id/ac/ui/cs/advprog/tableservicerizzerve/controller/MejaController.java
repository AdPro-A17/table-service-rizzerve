package id.ac.ui.cs.advprog.tableservicerizzerve.controller;

import id.ac.ui.cs.advprog.tableservicerizzerve.dto.*;
import id.ac.ui.cs.advprog.tableservicerizzerve.model.Meja;
import id.ac.ui.cs.advprog.tableservicerizzerve.service.MejaService;
import id.ac.ui.cs.advprog.tableservicerizzerve.repository.MejaRepository;
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
    private final MejaRepository mejaRepository;
    private static final String TABLE_NOT_FOUND = "Table not found";

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

    @GetMapping("/customer/all")
    public ResponseEntity<GetAllMejaCustomerResponse> getAllMejaForCustomer() {
        List<MejaCustomerViewDto> mejaList = mejaService.findAllMejaForCustomer();
        GetAllMejaCustomerResponse response = new GetAllMejaCustomerResponse("Retrieved all tables for customer view successfully", mejaList);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<MejaWithOrderResponse> getMejaById(@PathVariable UUID id) {
        MejaWithOrderResponse mejaWithOrderData = mejaService.findById(id);
        return ResponseEntity.ok(mejaWithOrderData);
    }

    @GetMapping("/nomor/{nomorMeja}")
    public ResponseEntity<GetMejaResponse> getMejaByNomor(@PathVariable int nomorMeja) {
        return mejaService.findByNomorMeja(nomorMeja)
                .map(m -> ResponseEntity.ok(new GetMejaResponse("Table found", m)))
                .orElseGet(() -> ResponseEntity.status(404).body(new GetMejaResponse(TABLE_NOT_FOUND, null)));
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
            return ResponseEntity.ok(new TableAvailabilityResponse(false, TABLE_NOT_FOUND));
        }
        Meja meja = mejaOpt.get();
        boolean available = meja.getStatus().equalsIgnoreCase("TERSEDIA");
        return ResponseEntity.ok(new TableAvailabilityResponse(available, available ? "TERSEDIA" : "TERPAKAI"));
    }

    @PutMapping("/update-status")
    public ResponseEntity<TableUpdateResponse> updateStatus(
            @RequestParam("tableNumber") int tableNumber,
            @RequestParam("status") String status,
            @RequestParam(value = "activeOrderId", required = false) String activeOrderId,
            @RequestParam(value = "activeOrderStatus", required = false) String activeOrderStatus) {
        Optional<Meja> mejaOpt = mejaService.findByNomorMeja(tableNumber);
        if (mejaOpt.isEmpty()) {
            return ResponseEntity.status(404).body(new TableUpdateResponse(TABLE_NOT_FOUND, null));
        }
        Meja meja = mejaOpt.get();
        
        // Update status
        meja.setStatus(MejaStatus.fromString(status));
        
        // Handle active order information
        if (activeOrderId != null && !activeOrderId.trim().isEmpty()) {
            try {
                meja.setActiveOrderId(UUID.fromString(activeOrderId));
            } catch (IllegalArgumentException e) {
                // Invalid UUID format, set to null
                meja.setActiveOrderId(null);
            }
        } else {
            meja.setActiveOrderId(null);
        }
        
        if (activeOrderStatus != null && !activeOrderStatus.trim().isEmpty()) {
            meja.setActiveOrderStatus(activeOrderStatus);
        } else {
            meja.setActiveOrderStatus(null);
        }
        
        // Save the meja directly with all updated fields
        Meja updatedMeja = mejaRepository.save(meja);
        
        return ResponseEntity.ok(new TableUpdateResponse("Status updated", updatedMeja));
    }
}