package id.ac.ui.cs.advprog.tableservicerizzerve.service;

import id.ac.ui.cs.advprog.tableservicerizzerve.model.Meja;

import java.util.List;
import java.util.UUID;

public interface MejaService {
    Meja createMeja(int nomorMeja, String status);
    List<Meja> findAllMeja();
    Meja updateMeja(UUID id, int nomor, String status);
    void deleteMeja(UUID id);
}