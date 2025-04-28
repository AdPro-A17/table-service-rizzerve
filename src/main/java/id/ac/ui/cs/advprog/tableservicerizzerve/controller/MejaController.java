package id.ac.ui.cs.advprog.tableservicerizzerve.controller;

import id.ac.ui.cs.advprog.tableservicerizzerve.dto.*;
import id.ac.ui.cs.advprog.tableservicerizzerve.model.Meja;
import id.ac.ui.cs.advprog.tableservicerizzerve.service.MejaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/meja")
@RequiredArgsConstructor
public class MejaController {

    private final MejaService mejaService;

    @PostMapping("/create")
    public ResponseEntity<CreateMejaResponse> createMeja(@RequestBody CreateMejaRequest request) {
        Meja meja = mejaService.createMeja(request.getNomorMeja(), request.getStatus());
        return ResponseEntity.status(201).body(new CreateMejaResponse("success", meja));
    }

    @GetMapping
    public ResponseEntity<GetAllMejaResponse> getAllMeja() {
        List<Meja> mejaList = mejaService.findAllMeja();
        GetAllMejaResponse response = new GetAllMejaResponse("success", mejaList);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<UpdateMejaResponse> updateMeja(@PathVariable UUID id, @RequestBody UpdateMejaRequest req){
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<DeleteMejaResponse> deleteMeja(@PathVariable UUID id){
    }
}