package id.ac.ui.cs.advprog.tableservicerizzerve.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.tableservicerizzerve.dto.OrderDetailsEvent;
import id.ac.ui.cs.advprog.tableservicerizzerve.enums.MejaStatus;
import id.ac.ui.cs.advprog.tableservicerizzerve.model.Meja;
import id.ac.ui.cs.advprog.tableservicerizzerve.dto.MejaEvent;
import id.ac.ui.cs.advprog.tableservicerizzerve.observer.MejaEventPublisher;
import id.ac.ui.cs.advprog.tableservicerizzerve.repository.MejaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
public class MejaOrderUpdaterServiceImpl implements MejaOrderUpdaterService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MejaOrderUpdaterServiceImpl.class);
    private final MejaRepository mejaRepository;
    private final ObjectMapper objectMapper;
    private final MejaEventPublisher mejaEventPublisher;

    @Autowired
    public MejaOrderUpdaterServiceImpl(MejaRepository mejaRepository, ObjectMapper objectMapper, MejaEventPublisher mejaEventPublisher) {
        this.mejaRepository = mejaRepository;
        this.objectMapper = objectMapper;
        this.mejaEventPublisher = mejaEventPublisher;
    }

    @Override
    @Transactional
    public void updateMejaWithActiveOrderDetails(OrderDetailsEvent orderEvent) {
        if (orderEvent.getTableNumber() == null) {
            LOGGER.warn("Received order event with null table number. OrderID: {}", orderEvent.getOrderId());
            return;
        }
        try {
            int nomorMeja = Integer.parseInt(orderEvent.getTableNumber());
            Optional<Meja> mejaOpt = mejaRepository.findByNomorMeja(nomorMeja);

            if (mejaOpt.isPresent()) {
                Meja meja = mejaOpt.get();
                meja.setActiveOrderId(orderEvent.getOrderId());
                meja.setActiveOrderStatus(orderEvent.getOrderStatus());
                meja.setActiveOrderTotalPrice(orderEvent.getTotalPrice());
                try {
                    meja.setActiveOrderItemsJson(objectMapper.writeValueAsString(orderEvent.getItems()));
                } catch (JsonProcessingException e) {
                    LOGGER.error("Error serializing order items to JSON for table {}: {}", nomorMeja, e.getMessage());
                    meja.setActiveOrderItemsJson("[]"); // Default ke array JSON kosong jika error
                }
                mejaRepository.save(meja);
                LOGGER.info("Updated Meja {} with active order details from OrderID {}", nomorMeja, orderEvent.getOrderId());
            } else {
                LOGGER.warn("Meja with number {} not found in TableService when processing order event from OrderID {}",
                        nomorMeja, orderEvent.getOrderId());
            }
        } catch (NumberFormatException e) {
            LOGGER.error("Invalid table number format '{}' from OrderID {}", orderEvent.getTableNumber(), orderEvent.getOrderId());
        }
    }

    @Override
    @Transactional
    public void clearActiveOrderFromMeja(String tableNumber) {
        if (tableNumber == null) {
            LOGGER.warn("Received clear active order request with null table number.");
            return;
        }
        try {
            int nomorMeja = Integer.parseInt(tableNumber);
            Optional<Meja> mejaOpt = mejaRepository.findByNomorMeja(nomorMeja);
            if (mejaOpt.isPresent()) {
                Meja meja = mejaOpt.get();
                meja.setActiveOrderId(null);
                meja.setActiveOrderStatus(null);
                meja.setActiveOrderTotalPrice(null);
                meja.setActiveOrderItemsJson(null);
                mejaRepository.save(meja);
                LOGGER.info("Cleared active order details from Meja {}", nomorMeja);
            } else {
                LOGGER.warn("Meja with number {} not found in TableService when trying to clear active order.", nomorMeja);
            }
        } catch (NumberFormatException e) {
            LOGGER.error("Invalid table number format '{}' when trying to clear active order.", tableNumber);
        }
    }

    @Override
    @Transactional
    public void handleOrderCreatedForTable(OrderDetailsEvent orderEvent) {
        if (orderEvent.getTableNumber() == null) {
            LOGGER.warn("Received ORDER_CREATED event with null table number. OrderID: {}", orderEvent.getOrderId());
            return;
        }
        LOGGER.info("Handling ORDER_CREATED event for table number: {}, OrderID: {}", orderEvent.getTableNumber(), orderEvent.getOrderId());
        try {
            int nomorMeja = Integer.parseInt(orderEvent.getTableNumber());
            Optional<Meja> mejaOpt = mejaRepository.findByNomorMeja(nomorMeja);

            if (mejaOpt.isPresent()) {
                Meja meja = mejaOpt.get();
                meja.setActiveOrderId(orderEvent.getOrderId());
                meja.setActiveOrderStatus(orderEvent.getOrderStatus());
                meja.setActiveOrderTotalPrice(orderEvent.getTotalPrice());
                try {
                    meja.setActiveOrderItemsJson(objectMapper.writeValueAsString(orderEvent.getItems()));
                } catch (JsonProcessingException e) {
                    LOGGER.error("Error serializing order items to JSON for table {} on order creation: {}", nomorMeja, e.getMessage());
                    meja.setActiveOrderItemsJson("[]");
                }

                if (!MejaStatus.TERPAKAI.getValue().equalsIgnoreCase(meja.getStatus())) {
                    meja.setStatus(MejaStatus.TERPAKAI);
                    LOGGER.info("Meja {} status set to TERPAKAI due to new order {}.", nomorMeja, orderEvent.getOrderId());
                    Meja updatedMeja = mejaRepository.save(meja);

                    mejaEventPublisher.publish(new MejaEvent(
                            MejaEvent.Type.UPDATED_STATUS,
                            updatedMeja.getId(),
                            updatedMeja.getNomorMeja(),
                            null,
                            updatedMeja.getStatus(),
                            Instant.now()
                    ));
                    LOGGER.info("Published MejaEvent.UPDATED_STATUS for Meja {} after order creation.", nomorMeja);
                } else {
                    mejaRepository.save(meja);
                    LOGGER.info("Meja {} was already TERPAKAI. Updated with active order details from OrderID {}.", nomorMeja, orderEvent.getOrderId());
                }
            } else {
                LOGGER.warn("Meja with number {} not found in TableService when processing ORDER_CREATED event from OrderID {}",
                        nomorMeja, orderEvent.getOrderId());
            }
        } catch (NumberFormatException e) {
            LOGGER.error("Invalid table number format '{}' from ORDER_CREATED event with OrderID {}", orderEvent.getTableNumber(), orderEvent.getOrderId());
        }
    }
}