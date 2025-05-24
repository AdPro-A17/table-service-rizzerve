package id.ac.ui.cs.advprog.tableservicerizzerve.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.tableservicerizzerve.dto.*;
import id.ac.ui.cs.advprog.tableservicerizzerve.enums.MejaStatus;
import id.ac.ui.cs.advprog.tableservicerizzerve.exception.*;
import id.ac.ui.cs.advprog.tableservicerizzerve.model.Meja;
import id.ac.ui.cs.advprog.tableservicerizzerve.observer.MejaEventPublisher;
import id.ac.ui.cs.advprog.tableservicerizzerve.repository.MejaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.core.type.TypeReference;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MejaServiceImpl implements MejaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MejaServiceImpl.class);

    private final MejaRepository mejaRepository;
    private final MejaEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

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

    @Override
    public MejaWithOrderResponse findById(UUID id) {
        Meja meja = mejaRepository.findById(id).orElseThrow(MejaNotFoundException::new);

        OrderDataForTableDto currentOrderDto = null;

        if (meja.getActiveOrderId() != null) {
            List<OrderItemSummaryDto> items = Collections.emptyList();

            if (meja.getActiveOrderItemsJson() != null && !meja.getActiveOrderItemsJson().isEmpty()) {
                try {
                    items = objectMapper.readValue(meja.getActiveOrderItemsJson(), new TypeReference<List<OrderItemSummaryDto>>() {});
                } catch (Exception e) {
                    LOGGER.error("Error deserializing active_order_items_json for mejaId {}: {}", meja.getId(), e.getMessage());
                }
            }

            currentOrderDto = OrderDataForTableDto.builder()
                    .orderId(meja.getActiveOrderId())
                    .orderStatus(meja.getActiveOrderStatus())
                    .totalPrice(meja.getActiveOrderTotalPrice() != null ? meja.getActiveOrderTotalPrice() : 0.0)
                    .items(items)
                    .build();
        }

        return MejaWithOrderResponse.builder()
                .mejaId(meja.getId())
                .nomorMeja(meja.getNomorMeja())
                .statusMeja(meja.getStatus())
                .currentOrder(currentOrderDto)
                .build();
    }

    @Override
    public List<MejaCustomerViewDto> findAllMejaForCustomer() {
        return mejaRepository.findAll().stream()
                .map(meja -> MejaCustomerViewDto.builder()
                        .nomorMeja(meja.getNomorMeja())
                        .statusMeja(meja.getStatus())
                        .build())
                .collect(Collectors.toList());
    }
}