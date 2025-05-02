package id.ac.ui.cs.advprog.tableservicerizzerve.service;

import id.ac.ui.cs.advprog.tableservicerizzerve.enums.MejaStatus;
import id.ac.ui.cs.advprog.tableservicerizzerve.model.Meja;
import id.ac.ui.cs.advprog.tableservicerizzerve.observer.MejaCreatedEvent;
import id.ac.ui.cs.advprog.tableservicerizzerve.observer.MejaDeletedEvent;
import id.ac.ui.cs.advprog.tableservicerizzerve.observer.MejaUpdatedEvent;
import id.ac.ui.cs.advprog.tableservicerizzerve.repository.MejaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MejaServiceImpl implements MejaService {

    private final MejaRepository mejaRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Meja createMeja(int nomorMeja, String status) {
        if (nomorMeja < 1) {
            throw new IllegalArgumentException("nomorMeja must be >= 1");
        }

        if (mejaRepository.findByNomorMeja(nomorMeja) != null) {
            throw new IllegalArgumentException("Nomor meja already exists");
        }

        MejaStatus mejaStatus = MejaStatus.fromString(status);
        Meja meja = new Meja(nomorMeja, mejaStatus.getValue());

        mejaRepository.save(meja);
        eventPublisher.publishEvent(new MejaCreatedEvent(this, meja));

        return meja;
    }

    @Override
    public List<Meja> findAllMeja() {
        return mejaRepository.findAll();
    }

    @Override
    public Meja updateMeja(UUID id, int nomor, String status) {
        if (nomor < 1) {
            throw new IllegalArgumentException("nomorMeja must be >= 1");
        }

        if (mejaRepository.findByNomorMeja(nomor) != null && !mejaRepository.findByNomorMeja(nomor).getId().equals(id)) {
            throw new IllegalArgumentException("Nomor meja already exists");
        }

        Meja existing = mejaRepository.findById(id);

        if (existing == null) {
            throw new IllegalArgumentException("Meja not found");
        }

        existing.setNomorMeja(nomor);
        existing.setStatus(MejaStatus.fromString(status));
        mejaRepository.update(existing);

        eventPublisher.publishEvent(new MejaUpdatedEvent(this, existing));
        return existing;
    }

    @Override
    public void deleteMeja(UUID id){
        mejaRepository.delete(id);
        eventPublisher.publishEvent(new MejaDeletedEvent(this, id));
    }
}