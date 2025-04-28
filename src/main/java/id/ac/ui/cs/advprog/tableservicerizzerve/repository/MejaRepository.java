package id.ac.ui.cs.advprog.tableservicerizzerve.repository;

import id.ac.ui.cs.advprog.tableservicerizzerve.model.Meja;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

@Repository
public class MejaRepository {

    private final Map<UUID, Meja> mejaStorage = new HashMap<>();

    public Meja save(Meja meja) {
        mejaStorage.put(meja.getId(), meja);
        return meja;
    }

    public List<Meja> findAll() {
        return new ArrayList<>(mejaStorage.values());
    }

    public Meja findById(UUID id) {
        return mejaStorage.get(id);
    }

    public Meja update(Meja meja){
        mejaStorage.put(meja.getId(), meja);
        return meja;
    }

    public void delete(UUID id){
        mejaStorage.remove(id);
    }
}