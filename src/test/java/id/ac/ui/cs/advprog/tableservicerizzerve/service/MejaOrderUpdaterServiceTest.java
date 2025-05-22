package id.ac.ui.cs.advprog.tableservicerizzerve.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.tableservicerizzerve.dto.MejaEvent;
import id.ac.ui.cs.advprog.tableservicerizzerve.dto.OrderDetailsEvent;
import id.ac.ui.cs.advprog.tableservicerizzerve.dto.OrderItemSummaryDto;
import id.ac.ui.cs.advprog.tableservicerizzerve.enums.MejaStatus;
import id.ac.ui.cs.advprog.tableservicerizzerve.observer.MejaEventPublisher;
import id.ac.ui.cs.advprog.tableservicerizzerve.model.Meja;
import id.ac.ui.cs.advprog.tableservicerizzerve.repository.MejaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MejaOrderUpdaterServiceImplTest {
    @Mock
    private MejaRepository mejaRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private MejaEventPublisher mejaEventPublisher;

    @InjectMocks
    private MejaOrderUpdaterServiceImpl mejaOrderUpdaterService;

    private OrderDetailsEvent orderEvent;
    private Meja meja;
    private UUID orderId;
    private UUID mejaId;
    private String tableNumberStr;
    private int tableNumberInt;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        mejaId = UUID.randomUUID();
        tableNumberInt = 101;
        tableNumberStr = String.valueOf(tableNumberInt);

        meja = new Meja(tableNumberInt, MejaStatus.TERSEDIA.getValue());
        meja.setId(mejaId);

        List<OrderItemSummaryDto> items = Collections.singletonList(
                OrderItemSummaryDto.builder().menuItemName("Test Item").quantity(1).price(10.0).subtotal(10.0).build()
        );

        orderEvent = OrderDetailsEvent.builder()
                .orderId(orderId)
                .tableNumber(tableNumberStr)
                .orderStatus("NEW")
                .totalPrice(10.0)
                .items(items)
                .eventType(OrderDetailsEvent.EventType.CREATED)
                .occurredAt(Instant.now())
                .build();
    }

    @Test
    void testUpdateMejaWithActiveOrderDetailsMejaFound() throws JsonProcessingException {
        String itemsJson = "[{\"menuItemName\":\"Test Item\",\"quantity\":1}]";
        when(mejaRepository.findByNomorMeja(tableNumberInt)).thenReturn(Optional.of(meja));
        when(objectMapper.writeValueAsString(orderEvent.getItems())).thenReturn(itemsJson);
        when(mejaRepository.save(any(Meja.class))).thenReturn(meja);

        mejaOrderUpdaterService.updateMejaWithActiveOrderDetails(orderEvent);

        assertEquals(orderId, meja.getActiveOrderId());
        assertEquals("NEW", meja.getActiveOrderStatus());
        assertEquals(10.0, meja.getActiveOrderTotalPrice());
        assertEquals(itemsJson, meja.getActiveOrderItemsJson());
        verify(mejaRepository).save(meja);
    }

    @Test
    void testUpdateMejaWithActiveOrderDetailsMejaNotFound() {
        when(mejaRepository.findByNomorMeja(tableNumberInt)).thenReturn(Optional.empty());

        mejaOrderUpdaterService.updateMejaWithActiveOrderDetails(orderEvent);

        verify(mejaRepository, never()).save(any(Meja.class));
    }

    @Test
    void testUpdateMejaWithActiveOrderDetailsNullTableNumber() {
        orderEvent.setTableNumber(null);
        mejaOrderUpdaterService.updateMejaWithActiveOrderDetails(orderEvent);
        verify(mejaRepository, never()).findByNomorMeja(anyInt());
        verify(mejaRepository, never()).save(any(Meja.class));
    }

    @Test
    void testUpdateMejaWithActiveOrderDetailsJsonProcessingError() throws JsonProcessingException {
        when(mejaRepository.findByNomorMeja(tableNumberInt)).thenReturn(Optional.of(meja));
        when(objectMapper.writeValueAsString(orderEvent.getItems())).thenThrow(new JsonProcessingException("Test Error") {});
        when(mejaRepository.save(any(Meja.class))).thenReturn(meja);

        mejaOrderUpdaterService.updateMejaWithActiveOrderDetails(orderEvent);

        assertEquals("[]", meja.getActiveOrderItemsJson());
        verify(mejaRepository).save(meja);
    }

    @Test
    void testClearActiveOrderFromMejaAlreadyTersedia() {
        meja.setActiveOrderId(orderId);
        meja.setStatus(MejaStatus.TERSEDIA);
        when(mejaRepository.findByNomorMeja(tableNumberInt)).thenReturn(Optional.of(meja));
        when(mejaRepository.save(any(Meja.class))).thenReturn(meja);

        mejaOrderUpdaterService.clearActiveOrderFromMeja(tableNumberStr);

        assertNull(meja.getActiveOrderId());
        assertEquals(MejaStatus.TERSEDIA.getValue(), meja.getStatus());
        verify(mejaRepository).save(meja);
        verify(mejaEventPublisher, never()).publish(any(MejaEvent.class));
    }

    @Test
    void testClearActiveOrderFromMejaNotFound() {
        when(mejaRepository.findByNomorMeja(tableNumberInt)).thenReturn(Optional.empty());
        mejaOrderUpdaterService.clearActiveOrderFromMeja(tableNumberStr);
        verify(mejaRepository, never()).save(any(Meja.class));
        verify(mejaEventPublisher, never()).publish(any(MejaEvent.class));
    }

    @Test
    void testClearActiveOrderFromMejaNullTableNumber() {
        mejaOrderUpdaterService.clearActiveOrderFromMeja(null);
        verify(mejaRepository, never()).findByNomorMeja(anyInt());
        verify(mejaRepository, never()).save(any(Meja.class));
        verify(mejaEventPublisher, never()).publish(any(MejaEvent.class));
    }

    @Test
    void testHandleOrderCreatedForMejaFoundAndTersedia() throws JsonProcessingException {
        String itemsJson = "[{}]";
        meja.setStatus(MejaStatus.TERSEDIA);
        when(mejaRepository.findByNomorMeja(tableNumberInt)).thenReturn(Optional.of(meja));
        when(objectMapper.writeValueAsString(orderEvent.getItems())).thenReturn(itemsJson);

        when(mejaRepository.save(any(Meja.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(mejaEventPublisher).publish(any(MejaEvent.class));

        mejaOrderUpdaterService.handleOrderCreatedForTable(orderEvent);

        assertEquals(orderId, meja.getActiveOrderId());
        assertEquals(MejaStatus.TERPAKAI.getValue(), meja.getStatus());
        verify(mejaRepository, times(1)).save(meja);
        verify(mejaEventPublisher).publish(argThat(event ->
                event.getType() == MejaEvent.Type.UPDATED_STATUS && event.getStatus().equals(MejaStatus.TERPAKAI.getValue()) && event.getNomorMeja().equals(tableNumberInt)
        ));
    }

    @Test
    void testHandleOrderCreatedForMejaFoundAndAlreadyTerpakai() throws JsonProcessingException {
        String itemsJson = "[{}]";
        meja.setStatus(MejaStatus.TERPAKAI);
        when(mejaRepository.findByNomorMeja(tableNumberInt)).thenReturn(Optional.of(meja));
        when(objectMapper.writeValueAsString(orderEvent.getItems())).thenReturn(itemsJson);
        when(mejaRepository.save(any(Meja.class))).thenReturn(meja);

        mejaOrderUpdaterService.handleOrderCreatedForTable(orderEvent);

        assertEquals(orderId, meja.getActiveOrderId());
        assertEquals(MejaStatus.TERPAKAI.getValue(), meja.getStatus());
        verify(mejaRepository, times(1)).save(meja);
        verify(mejaEventPublisher, never()).publish(any(MejaEvent.class));
    }

    @Test
    void testHandleOrderCreatedForTableMejaNotFound() {
        when(mejaRepository.findByNomorMeja(tableNumberInt)).thenReturn(Optional.empty());
        mejaOrderUpdaterService.handleOrderCreatedForTable(orderEvent);
        verify(mejaRepository, never()).save(any(Meja.class));
        verify(mejaEventPublisher, never()).publish(any(MejaEvent.class));
    }

    @Test
    void testHandleOrderCreatedForTableNullTableNumber() {
        orderEvent.setTableNumber(null);
        mejaOrderUpdaterService.handleOrderCreatedForTable(orderEvent);
        verify(mejaRepository, never()).findByNomorMeja(anyInt());
        verify(mejaRepository, never()).save(any(Meja.class));
        verify(mejaEventPublisher, never()).publish(any(MejaEvent.class));
    }
}