package id.ac.ui.cs.advprog.tableservicerizzerve.service;

import id.ac.ui.cs.advprog.tableservicerizzerve.enums.MejaStatus;
import id.ac.ui.cs.advprog.tableservicerizzerve.exception.DuplicateNomorMejaException;
import id.ac.ui.cs.advprog.tableservicerizzerve.exception.InvalidNomorMejaException;
import id.ac.ui.cs.advprog.tableservicerizzerve.exception.MejaNotFoundException;
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
            throw new InvalidNomorMejaException();
        }

        if (mejaRepository.findByNomorMeja(nomorMeja).isPresent()) {
            throw new DuplicateNomorMejaException();
        }

        Meja meja = new Meja(nomorMeja, MejaStatus.fromString(status).getValue());
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
        Meja existing = mejaRepository.findById(id).orElseThrow(MejaNotFoundException::new);

        if (nomor < 1) {
            throw new InvalidNomorMejaException();
        }
        mejaRepository.findByNomorMeja(nomor).ifPresent(conflict -> {
            if (!conflict.getId().equals(id)) {
                throw new DuplicateNomorMejaException();
            }
        });

        existing.setNomorMeja(nomor);
        existing.setStatus(MejaStatus.fromString(status));
        Meja updated = mejaRepository.save(existing);
        eventPublisher.publishEvent(new MejaUpdatedEvent(this, updated));
        return updated;
    }

    @Override
    public void deleteMeja(UUID id) {
        if (!mejaRepository.existsById(id)) {
            throw new MejaNotFoundException();
        }
        mejaRepository.deleteById(id);
        eventPublisher.publishEvent(new MejaDeletedEvent(this, id));
    }
}