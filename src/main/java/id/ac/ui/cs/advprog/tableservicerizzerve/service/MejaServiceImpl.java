package id.ac.ui.cs.advprog.tableservicerizzerve.service;

import id.ac.ui.cs.advprog.tableservicerizzerve.enums.MejaStatus;
import id.ac.ui.cs.advprog.tableservicerizzerve.model.Meja;
import id.ac.ui.cs.advprog.tableservicerizzerve.observer.MejaCreatedEvent;
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

        MejaStatus mejaStatus = MejaStatus.fromString(status);
        Meja meja = new Meja(nomorMeja, mejaStatus.getValue());

        mejaRepository.save(meja);
        // Observer Implementation
        eventPublisher.publishEvent(new MejaCreatedEvent(this, meja));

        return meja;
    }

    @Override
    public List<Meja> findAllMeja() {
        return mejaRepository.findAll();
    }

    @Override
    public Meja updateMeja(UUID id, int nomor, String status) {
    }

    @Override
    public void deleteMeja(UUID id){
    }
}