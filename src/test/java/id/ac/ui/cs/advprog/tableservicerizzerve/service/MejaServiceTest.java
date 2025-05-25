package id.ac.ui.cs.advprog.tableservicerizzerve.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.tableservicerizzerve.dto.*;
import id.ac.ui.cs.advprog.tableservicerizzerve.enums.MejaStatus;
import id.ac.ui.cs.advprog.tableservicerizzerve.exception.DuplicateNomorMejaException;
import id.ac.ui.cs.advprog.tableservicerizzerve.exception.InvalidMejaStatusException;
import id.ac.ui.cs.advprog.tableservicerizzerve.exception.InvalidNomorMejaException;
import id.ac.ui.cs.advprog.tableservicerizzerve.exception.MejaNotFoundException;
import id.ac.ui.cs.advprog.tableservicerizzerve.observer.MejaEventPublisher;
import id.ac.ui.cs.advprog.tableservicerizzerve.model.Meja;
import id.ac.ui.cs.advprog.tableservicerizzerve.repository.MejaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MejaServiceTest {

    @Mock
    private MejaRepository mejaRepository;

    @Mock
    private MejaEventPublisher eventPublisher;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private MejaServiceImpl mejaService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testCreateMejaSuccess() {
        when(mejaRepository.findByNomorMeja(5)).thenReturn(Optional.empty());
        when(mejaRepository.save(any(Meja.class))).thenAnswer(i -> {
            Meja meja = i.getArgument(0);
            meja.setId(UUID.randomUUID());
            return meja;
        });
        doNothing().when(eventPublisher).publish(any(MejaEvent.class));

        Meja result = mejaService.createMeja(5, MejaStatus.TERSEDIA.getValue());

        assertNotNull(result.getId());
        assertEquals(5, result.getNomorMeja());
        assertEquals(MejaStatus.TERSEDIA.getValue(), result.getStatus());
        verify(mejaRepository).findByNomorMeja(5);
        verify(mejaRepository).save(any(Meja.class));
        verify(eventPublisher).publish(any(MejaEvent.class));
    }

    @Test
    void testCreateMejaWithNullStatusDefaultsToTersedia() {
        when(mejaRepository.findByNomorMeja(1)).thenReturn(Optional.empty());
        when(mejaRepository.save(any(Meja.class))).thenAnswer(i -> i.getArgument(0));
        doNothing().when(eventPublisher).publish(any(MejaEvent.class));

        Meja result = mejaService.createMeja(1, null);

        assertEquals(MejaStatus.TERSEDIA.getValue(), result.getStatus());
    }


    @Test
    void testCreateMejaInvalidNomorShouldThrow() {
        String validStatus = MejaStatus.TERSEDIA.getValue();
        assertThrows(InvalidNomorMejaException.class, () -> mejaService.createMeja(0, validStatus));
        verify(eventPublisher, never()).publish(any(MejaEvent.class));
    }

    @Test
    void testCreateMejaInvalidStatusShouldThrow() {
        when(mejaRepository.findByNomorMeja(1)).thenReturn(Optional.empty());
        assertThrows(InvalidMejaStatusException.class, () -> mejaService.createMeja(1, "BAD_STATUS"));
        verify(eventPublisher, never()).publish(any(MejaEvent.class));
    }

    @Test
    void testCreateMejaDuplicateNomorShouldThrow() {
        Meja existingMeja = new Meja(5, MejaStatus.TERSEDIA.getValue());
        when(mejaRepository.findByNomorMeja(5)).thenReturn(Optional.of(existingMeja));
        assertThrows(DuplicateNomorMejaException.class, () -> mejaService.createMeja(5, MejaStatus.TERSEDIA.getValue()));
        verify(eventPublisher, never()).publish(any(MejaEvent.class));
    }

    @Test
    void testFindAllMeja() {
        Meja m1 = new Meja(1, MejaStatus.TERSEDIA.getValue());
        Meja m2 = new Meja(2, MejaStatus.TERSEDIA.getValue());
        when(mejaRepository.findAll()).thenReturn(List.of(m1, m2));

        List<Meja> list = mejaService.findAllMeja();

        assertEquals(2, list.size());
        assertTrue(list.stream().anyMatch(m -> m.getNomorMeja() == 1));
        assertTrue(list.stream().anyMatch(m -> m.getNomorMeja() == 2));
        verify(mejaRepository).findAll();
    }

    @Test
    void testUpdateMejaSuccessNomorAndStatusChanged() {
        UUID id = UUID.randomUUID();
        Meja stored = new Meja(1, MejaStatus.TERSEDIA.getValue());
        stored.setId(id);

        when(mejaRepository.findById(id)).thenReturn(Optional.of(stored));
        when(mejaRepository.findByNomorMeja(9)).thenReturn(Optional.empty());
        when(mejaRepository.save(any(Meja.class))).thenAnswer(i -> i.getArgument(0));
        doNothing().when(eventPublisher).publish(any(MejaEvent.class));

        Meja result = mejaService.updateMeja(id, 9, MejaStatus.TERPAKAI.getValue());

        assertEquals(9, result.getNomorMeja());
        assertEquals(MejaStatus.TERPAKAI.getValue(), result.getStatus());
        verify(eventPublisher, times(2)).publish(any(MejaEvent.class));
    }

    @Test
    void testUpdateMejaSuccessOnlyNomorChanged() {
        UUID id = UUID.randomUUID();
        Meja stored = new Meja(1, MejaStatus.TERSEDIA.getValue());
        stored.setId(id);

        when(mejaRepository.findById(id)).thenReturn(Optional.of(stored));
        when(mejaRepository.findByNomorMeja(9)).thenReturn(Optional.empty());
        when(mejaRepository.save(any(Meja.class))).thenAnswer(i -> i.getArgument(0));
        doNothing().when(eventPublisher).publish(any(MejaEvent.class));

        Meja result = mejaService.updateMeja(id, 9, MejaStatus.TERSEDIA.getValue());

        assertEquals(9, result.getNomorMeja());
        assertEquals(MejaStatus.TERSEDIA.getValue(), result.getStatus());
        verify(eventPublisher, times(1)).publish(argThat(event -> event.getType() == MejaEvent.Type.UPDATED_NOMOR));
    }

    @Test
    void testUpdateMejaSuccessOnlyStatusChanged() {
        UUID id = UUID.randomUUID();
        Meja stored = new Meja(1, MejaStatus.TERSEDIA.getValue());
        stored.setId(id);

        when(mejaRepository.findById(id)).thenReturn(Optional.of(stored));
        when(mejaRepository.save(any(Meja.class))).thenAnswer(i -> i.getArgument(0));
        doNothing().when(eventPublisher).publish(any(MejaEvent.class));

        Meja result = mejaService.updateMeja(id, 1, MejaStatus.TERPAKAI.getValue());

        assertEquals(1, result.getNomorMeja());
        assertEquals(MejaStatus.TERPAKAI.getValue(), result.getStatus());
        verify(eventPublisher, times(1)).publish(argThat(event -> event.getType() == MejaEvent.Type.UPDATED_STATUS));
    }

    @Test
    void testUpdateMejaDuplicateNomorThrows() {
        UUID id = UUID.randomUUID();
        Meja stored   = new Meja(3,  MejaStatus.TERSEDIA.getValue());
        stored.setId(id);
        Meja conflict = new Meja(7,  MejaStatus.TERSEDIA.getValue());
        conflict.setId(UUID.randomUUID());

        when(mejaRepository.findById(id)).thenReturn(Optional.of(stored));
        when(mejaRepository.findByNomorMeja(7)).thenReturn(Optional.of(conflict));

        String validStatus = MejaStatus.TERSEDIA.getValue();
        assertThrows(DuplicateNomorMejaException.class, () -> mejaService.updateMeja(id, 7, validStatus));
        verify(eventPublisher, never()).publish(any(MejaEvent.class));
    }

    @Test
    void testUpdateMejaNotFoundThrows() {
        UUID id = UUID.randomUUID();
        when(mejaRepository.findById(id)).thenReturn(Optional.empty());

        String validStatus = MejaStatus.TERSEDIA.getValue();
        assertThrows(MejaNotFoundException.class, () -> mejaService.updateMeja(id, 5, validStatus));
        verify(eventPublisher, never()).publish(any(MejaEvent.class));
    }

    @Test
    void testUpdateMejaInvalidNomorThrows() {
        UUID id = UUID.randomUUID();
        Meja stored = new Meja(1, MejaStatus.TERSEDIA.getValue());
        stored.setId(id);
        when(mejaRepository.findById(id)).thenReturn(Optional.of(stored));

        String validStatus = MejaStatus.TERSEDIA.getValue();
        assertThrows(InvalidNomorMejaException.class, () -> mejaService.updateMeja(id, 0, validStatus));
        verify(eventPublisher, never()).publish(any(MejaEvent.class));
    }

    @Test
    void testDeleteMejaSuccess() {
        UUID id = UUID.randomUUID();
        Meja existing = new Meja(4, MejaStatus.TERSEDIA.getValue());
        existing.setId(id);
        when(mejaRepository.findById(id)).thenReturn(Optional.of(existing));
        doNothing().when(mejaRepository).delete(existing); // Mock void method
        doNothing().when(eventPublisher).publish(any(MejaEvent.class));

        mejaService.deleteMeja(id);

        verify(mejaRepository).delete(existing);
        verify(eventPublisher).publish(any(MejaEvent.class));
    }

    @Test
    void testDeleteMejaNotFoundThrows() {
        UUID id = UUID.randomUUID();
        when(mejaRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(MejaNotFoundException.class, () -> mejaService.deleteMeja(id));
        verify(eventPublisher, never()).publish(any(MejaEvent.class));
    }

    @Test
    void testFindByNomorMejaReturnsMeja() {
        Meja meja = new Meja(10, MejaStatus.TERSEDIA.getValue());
        when(mejaRepository.findByNomorMeja(10)).thenReturn(Optional.of(meja));

        Optional<Meja> result = mejaService.findByNomorMeja(10);

        assertTrue(result.isPresent());
        assertEquals(10, result.get().getNomorMeja());
        verify(mejaRepository).findByNomorMeja(10);
    }

    @Test
    void testFindByNomorMejaReturnsEmpty() {
        when(mejaRepository.findByNomorMeja(99)).thenReturn(Optional.empty());

        Optional<Meja> result = mejaService.findByNomorMeja(99);

        assertTrue(result.isEmpty());
        verify(mejaRepository).findByNomorMeja(99);
    }

    @Test
    void testUpdateMejaInvalidStatusThrows() {
        UUID id = UUID.randomUUID();
        Meja stored = new Meja(1, MejaStatus.TERSEDIA.getValue());
        stored.setId(id);
        when(mejaRepository.findById(id)).thenReturn(Optional.of(stored));

        assertThrows(InvalidMejaStatusException.class, () -> mejaService.updateMeja(id, 1, "BAD_STATUS"));
        verify(eventPublisher, never()).publish(any(MejaEvent.class));
    }

    @Test
    void testFindAllMejaEmpty() {
        when(mejaRepository.findAll()).thenReturn(Collections.emptyList());

        List<Meja> result = mejaService.findAllMeja();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mejaRepository).findAll();
    }

    @Test
    void testFindByIdMejaFoundNoActiveOrder() throws IOException {
        UUID mejaId = UUID.randomUUID();
        Meja meja = new Meja(1, MejaStatus.TERSEDIA.getValue());
        meja.setId(mejaId);

        when(mejaRepository.findById(mejaId)).thenReturn(Optional.of(meja));

        MejaWithOrderResponse response = mejaService.findById(mejaId);

        assertNotNull(response);
        assertEquals(mejaId, response.getMejaId());
        assertEquals(1, response.getNomorMeja());
        assertEquals(MejaStatus.TERSEDIA.getValue(), response.getStatusMeja());
        assertNull(response.getCurrentOrder());
        verify(mejaRepository).findById(mejaId);
        verify(objectMapper, never()).readValue(anyString(), any(TypeReference.class));
    }

    @Test
    void testFindByIdMejaFoundWithActiveOrder() throws IOException {
        UUID mejaId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        Meja meja = new Meja(2, MejaStatus.TERPAKAI.getValue());
        meja.setId(mejaId);
        meja.setActiveOrderId(orderId);
        meja.setActiveOrderStatus("PROCESSING");
        meja.setActiveOrderTotalPrice(150.0);
        String itemsJson = "[{\"menuItemId\":\"ec0889ee-50c9-4c7e-8504-d73abeded35f\",\"menuItemName\":\"Nasi Goreng\",\"quantity\":1,\"price\":75.0,\"subtotal\":75.0}," +
                "{\"menuItemId\":\"40581f2b-fa1d-4ab2-8f3f-5c233d60e8e9\",\"menuItemName\":\"Es Teh\",\"quantity\":1,\"price\":75.0,\"subtotal\":75.0}]";
        meja.setActiveOrderItemsJson(itemsJson);

        List<OrderItemSummaryDto> expectedItems = List.of(
                OrderItemSummaryDto.builder().menuItemId(UUID.fromString("ec0889ee-50c9-4c7e-8504-d73abeded35f")).menuItemName("Nasi Goreng").quantity(1).price(75.0).subtotal(75.0).build(),
                OrderItemSummaryDto.builder().menuItemId(UUID.fromString("40581f2b-fa1d-4ab2-8f3f-5c233d60e8e9")).menuItemName("Es Teh").quantity(1).price(75.0).subtotal(75.0).build()
        );

        when(mejaRepository.findById(mejaId)).thenReturn(Optional.of(meja));
        when(objectMapper.readValue(eq(itemsJson), any(TypeReference.class))).thenReturn(expectedItems);

        MejaWithOrderResponse response = mejaService.findById(mejaId);

        assertNotNull(response);
        assertEquals(mejaId, response.getMejaId());
        assertEquals(2, response.getNomorMeja());
        assertEquals(MejaStatus.TERPAKAI.getValue(), response.getStatusMeja());

        assertNotNull(response.getCurrentOrder());
        OrderDataForTableDto currentOrder = response.getCurrentOrder();
        assertEquals(orderId, currentOrder.getOrderId());
        assertEquals("PROCESSING", currentOrder.getOrderStatus());
        assertEquals(150.0, currentOrder.getTotalPrice());
        assertNotNull(currentOrder.getItems());
        assertEquals(2, currentOrder.getItems().size());
        assertEquals("Nasi Goreng", currentOrder.getItems().get(0).getMenuItemName());

        verify(mejaRepository).findById(mejaId);
        verify(objectMapper).readValue(eq(itemsJson), any(TypeReference.class));
    }

    @Test
    void testFindByIdMejaFoundActiveOrder() throws IOException {
        UUID mejaId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        Meja meja = new Meja(3, MejaStatus.TERPAKAI.getValue());
        meja.setId(mejaId);
        meja.setActiveOrderId(orderId);
        meja.setActiveOrderStatus("NEW");
        meja.setActiveOrderTotalPrice(0.0);
        meja.setActiveOrderItemsJson(null);

        when(mejaRepository.findById(mejaId)).thenReturn(Optional.of(meja));

        MejaWithOrderResponse response = mejaService.findById(mejaId);

        assertNotNull(response);
        assertEquals(mejaId, response.getMejaId());
        assertNotNull(response.getCurrentOrder());
        assertEquals(orderId, response.getCurrentOrder().getOrderId());
        assertTrue(response.getCurrentOrder().getItems().isEmpty());

        verify(mejaRepository).findById(mejaId);
        verify(objectMapper, never()).readValue(anyString(), any(TypeReference.class));
    }

    @Test
    void testFindByIdMejaNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        when(mejaRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(MejaNotFoundException.class, () -> mejaService.findById(nonExistentId));
        verify(mejaRepository).findById(nonExistentId);
    }

    @Test
    void testUpdateMejaSameNomorNoConflict() {
        UUID id = UUID.randomUUID();
        Meja stored = new Meja(5, MejaStatus.TERSEDIA.getValue());
        stored.setId(id);

        when(mejaRepository.findById(id)).thenReturn(Optional.of(stored));
        when(mejaRepository.findByNomorMeja(5)).thenReturn(Optional.of(stored));
        when(mejaRepository.save(any(Meja.class))).thenAnswer(i -> i.getArgument(0));
        doNothing().when(eventPublisher).publish(any(MejaEvent.class));

        Meja result = mejaService.updateMeja(id, 5, MejaStatus.TERPAKAI.getValue());

        assertEquals(5, result.getNomorMeja());
        assertEquals(MejaStatus.TERPAKAI.getValue(), result.getStatus());
        verify(eventPublisher, times(1)).publish(argThat(event -> event.getType() == MejaEvent.Type.UPDATED_STATUS));
    }

    @Test
    void testFindByIdWithEmptyActiveOrderItemsJson() throws IOException {
        UUID mejaId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        Meja meja = new Meja(4, MejaStatus.TERPAKAI.getValue());
        meja.setId(mejaId);
        meja.setActiveOrderId(orderId);
        meja.setActiveOrderStatus("NEW");
        meja.setActiveOrderTotalPrice(100.0);
        meja.setActiveOrderItemsJson("");
        when(mejaRepository.findById(mejaId)).thenReturn(Optional.of(meja));

        MejaWithOrderResponse response = mejaService.findById(mejaId);

        assertNotNull(response);
        assertNotNull(response.getCurrentOrder());
        assertTrue(response.getCurrentOrder().getItems().isEmpty());
        verify(objectMapper, never()).readValue(anyString(), any(TypeReference.class));
    }

    @Test
    void testFindByIdWithJsonDeserializationError() throws IOException {
        UUID mejaId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        Meja meja = new Meja(5, MejaStatus.TERPAKAI.getValue());
        meja.setId(mejaId);
        meja.setActiveOrderId(orderId);
        meja.setActiveOrderStatus("PROCESSING");
        meja.setActiveOrderTotalPrice(200.0);
        meja.setActiveOrderItemsJson("{invalid json}");
        when(mejaRepository.findById(mejaId)).thenReturn(Optional.of(meja));
        when(objectMapper.readValue(eq("{invalid json}"), any(TypeReference.class))).thenThrow(new RuntimeException("JSON parsing error"));

        MejaWithOrderResponse response = mejaService.findById(mejaId);

        assertNotNull(response);
        assertNotNull(response.getCurrentOrder());
        assertTrue(response.getCurrentOrder().getItems().isEmpty());
        verify(objectMapper).readValue(eq("{invalid json}"), any(TypeReference.class));
    }

    @Test
    void testFindByIdWithNullActiveOrderTotalPrice() {
        UUID mejaId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        Meja meja = new Meja(6, MejaStatus.TERPAKAI.getValue());
        meja.setId(mejaId);
        meja.setActiveOrderId(orderId);
        meja.setActiveOrderStatus("NEW");
        meja.setActiveOrderTotalPrice(null);
        meja.setActiveOrderItemsJson(null);
        when(mejaRepository.findById(mejaId)).thenReturn(Optional.of(meja));

        MejaWithOrderResponse response = mejaService.findById(mejaId);

        assertNotNull(response);
        assertNotNull(response.getCurrentOrder());
        assertEquals(0.0, response.getCurrentOrder().getTotalPrice());
        verify(mejaRepository).findById(mejaId);
    }

    @Test
    void testFindAllMejaForCustomer() {
        Meja m1 = new Meja(1, MejaStatus.TERSEDIA.getValue());
        Meja m2 = new Meja(2, MejaStatus.TERPAKAI.getValue());
        when(mejaRepository.findAll()).thenReturn(List.of(m1, m2));

        List<MejaCustomerViewDto> result = mejaService.findAllMejaForCustomer();

        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getNomorMeja());
        assertEquals(MejaStatus.TERSEDIA.getValue(), result.get(0).getStatusMeja());
        assertEquals(2, result.get(1).getNomorMeja());
        assertEquals(MejaStatus.TERPAKAI.getValue(), result.get(1).getStatusMeja());
        verify(mejaRepository).findAll();
    }

    @Test
    void testFindAllMejaForCustomerEmpty() {
        when(mejaRepository.findAll()).thenReturn(Collections.emptyList());

        List<MejaCustomerViewDto> result = mejaService.findAllMejaForCustomer();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mejaRepository).findAll();
    }
}