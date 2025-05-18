package id.ac.ui.cs.advprog.tableservicerizzerve.service;

import id.ac.ui.cs.advprog.tableservicerizzerve.enums.MejaStatus;
import id.ac.ui.cs.advprog.tableservicerizzerve.exception.*;
import id.ac.ui.cs.advprog.tableservicerizzerve.model.Meja;
import id.ac.ui.cs.advprog.tableservicerizzerve.observer.MejaEvent;
import id.ac.ui.cs.advprog.tableservicerizzerve.observer.MejaEventPublisher;
import id.ac.ui.cs.advprog.tableservicerizzerve.repository.MejaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MejaServiceImpl implements MejaService {

    private final MejaRepository mejaRepository;
    private final MejaEventPublisher eventPublisher;

    @Override
    @Transactional
    public Meja createMeja(int nomorMeja, String status) {
        if (nomorMeja < 1) {
            throw new InvalidNomorMejaException();
        }
        mejaRepository.findByNomorMeja(nomorMeja).ifPresent(m -> {
            throw new DuplicateNomorMejaException();
        });

        Meja meja = new Meja(nomorMeja, MejaStatus.fromString(status).getValue());
        Meja saved = mejaRepository.save(meja);

        eventPublisher.publish(new MejaEvent(MejaEvent.Type.CREATED, saved.getId(), saved.getNomorMeja(), null, saved.getStatus(), Instant.now()));
        return saved;
    }

    @Override
    public List<Meja> findAllMeja() {
        return mejaRepository.findAll();
    }

    @Override
    @Transactional
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

        int oldNomor = existing.getNomorMeja();
        boolean nomorChanged = nomor != oldNomor;
        boolean statusChanged = !existing.getStatus().equalsIgnoreCase(status);

        existing.setNomorMeja(nomor);
        existing.setStatus(MejaStatus.fromString(status));
        Meja updated = mejaRepository.save(existing);

        Instant now = Instant.now();
        if (nomorChanged) {
            eventPublisher.publish(new MejaEvent(MejaEvent.Type.UPDATED_NOMOR, updated.getId(), updated.getNomorMeja(), oldNomor, updated.getStatus(), now));
        }
        if (statusChanged) {
            eventPublisher.publish(new MejaEvent(MejaEvent.Type.UPDATED_STATUS, updated.getId(), updated.getNomorMeja(), null, updated.getStatus(), now));
        }
        return updated;
    }

    @Override
    @Transactional
    public void deleteMeja(UUID id) {
        Meja meja = mejaRepository.findById(id).orElseThrow(MejaNotFoundException::new);
        mejaRepository.delete(meja);
        eventPublisher.publish(new MejaEvent(MejaEvent.Type.DELETED, meja.getId(), meja.getNomorMeja(), null, meja.getStatus(), Instant.now()));
    }

    @Override
    public Optional<Meja> findByNomorMeja(int nomorMeja) {
        return mejaRepository.findByNomorMeja(nomorMeja);
    }
}