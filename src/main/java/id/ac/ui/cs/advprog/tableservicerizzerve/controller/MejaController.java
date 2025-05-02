package id.ac.ui.cs.advprog.tableservicerizzerve.controller;

import id.ac.ui.cs.advprog.tableservicerizzerve.dto.*;
import id.ac.ui.cs.advprog.tableservicerizzerve.model.Meja;
import id.ac.ui.cs.advprog.tableservicerizzerve.service.MejaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
}