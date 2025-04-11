package id.ac.ui.cs.advprog.tableservicerizzerve.repository;

import id.ac.ui.cs.advprog.tableservicerizzerve.model.Meja;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class MejaRepository {
    private final Map<UUID, Meja> mejaStorage = new ConcurrentHashMap<>();

    public Meja save(Meja meja) {
        mejaStorage.put(meja.getId(), meja);
        return meja;
    }
}