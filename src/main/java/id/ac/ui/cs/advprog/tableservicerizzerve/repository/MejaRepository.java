package id.ac.ui.cs.advprog.tableservicerizzerve.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import id.ac.ui.cs.advprog.tableservicerizzerve.model.Meja;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MejaRepository extends JpaRepository<Meja, UUID> {
    Optional<Meja> findByNomorMeja(int nomorMeja);
}