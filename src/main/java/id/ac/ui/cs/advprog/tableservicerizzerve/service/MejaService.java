package id.ac.ui.cs.advprog.tableservicerizzerve.service;

import id.ac.ui.cs.advprog.tableservicerizzerve.model.Meja;

import java.util.List;

public interface MejaService {
    Meja createMeja(int nomorMeja, String status);
    List<Meja> findAllMeja();
}