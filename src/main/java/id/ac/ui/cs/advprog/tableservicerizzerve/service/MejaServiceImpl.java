package id.ac.ui.cs.advprog.tableservicerizzerve.service;

import id.ac.ui.cs.advprog.tableservicerizzerve.model.Meja;
import id.ac.ui.cs.advprog.tableservicerizzerve.repository.MejaRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class MejaServiceImpl implements MejaService {

    private final MejaRepository mejaRepository;
    private final ApplicationEventPublisher eventPublisher;

    public MejaServiceImpl(MejaRepository mejaRepository, ApplicationEventPublisher eventPublisher) {
        this.mejaRepository = mejaRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Meja createMeja(int nomorMeja, String status) {
        return null;
    }
}