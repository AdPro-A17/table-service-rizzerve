package id.ac.ui.cs.advprog.tableservicerizzerve.service;

import id.ac.ui.cs.advprog.tableservicerizzerve.enums.MejaStatus;
import id.ac.ui.cs.advprog.tableservicerizzerve.model.Meja;
import id.ac.ui.cs.advprog.tableservicerizzerve.observer.MejaCreatedEvent;
import id.ac.ui.cs.advprog.tableservicerizzerve.observer.MejaDeletedEvent;
import id.ac.ui.cs.advprog.tableservicerizzerve.observer.MejaUpdatedEvent;
import id.ac.ui.cs.advprog.tableservicerizzerve.repository.MejaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.UUID;

class MejaServiceTest {

    private MejaRepository mejaRepository;
    private ApplicationEventPublisher eventPublisher;
    private MejaServiceImpl mejaService;

    @BeforeEach
    void setUp() {
        mejaRepository = mock(MejaRepository.class);
        eventPublisher = mock(ApplicationEventPublisher.class);
        mejaService = new MejaServiceImpl(mejaRepository, eventPublisher);
    }

    @Test
    void testCreateMejaSuccess() {
        Meja meja = new Meja(5, "TERSEDIA");
        when(mejaRepository.save(any(Meja.class))).thenReturn(meja);

        Meja result = mejaService.createMeja(5, "TERSEDIA");

        assertNotNull(result.getId());
        assertEquals(5, result.getNomorMeja());
        assertEquals("TERSEDIA", result.getStatus());

        verify(mejaRepository, times(1)).save(any(Meja.class));
        verify(eventPublisher, times(1)).publishEvent(any(MejaCreatedEvent.class));
    }

    @Test
    void testCreateMejaInvalidNomorMejaShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> mejaService.createMeja(0, "TERSEDIA"));
    }

    @Test
    void testCreateMejaInvalidStatusShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> mejaService.createMeja(1, "INVALID_STATUS"));
    }

    @Test
    void createMejaDuplicateNomor() {
        when(mejaRepository.findByNomorMeja(5)).thenReturn(new Meja(5, MejaStatus.TERSEDIA.getValue()));
        assertThrows(IllegalArgumentException.class, () -> mejaService.createMeja(5, "TERSEDIA"));
    }

    @Test
    void testFindAllMejaReturnsListOfMeja() {
        Meja meja1 = new Meja(1, "TERSEDIA");
        Meja meja2 = new Meja(2, "TERSEDIA");

        when(mejaRepository.findAll()).thenReturn(List.of(meja1, meja2));

        List<Meja> result = mejaService.findAllMeja();

        assertEquals(2, result.size());
        assertTrue(result.contains(meja1));
        assertTrue(result.contains(meja2));

        verify(mejaRepository, times(1)).findAll();
    }

    @Test
    void testUpdateMejaSuccess() {
        UUID id = UUID.randomUUID();

        Meja stored = new Meja(1, MejaStatus.TERSEDIA.getValue());
        stored.setId(id);

        when(mejaRepository.findById(id)).thenReturn(stored);

        Meja result = mejaService.updateMeja(id, 9, "TERPAKAI");

        assertEquals(9, result.getNomorMeja());
        assertEquals(MejaStatus.TERPAKAI.getValue(), result.getStatus());
        verify(eventPublisher).publishEvent(any(MejaUpdatedEvent.class));
    }

    @Test
    void updateTableWithExistingNumber() {
        UUID currentTableId = UUID.randomUUID();
        UUID existingTableId = UUID.randomUUID();

        Meja currentTable = new Meja(3, MejaStatus.TERSEDIA.getValue());
        currentTable.setId(currentTableId);
        Meja existingTable = new Meja(7, MejaStatus.TERSEDIA.getValue());
        existingTable.setId(existingTableId);

        when(mejaRepository.findById(currentTableId)).thenReturn(currentTable);
        when(mejaRepository.findByNomorMeja(7)).thenReturn(existingTable);
        assertThrows(IllegalArgumentException.class, () -> mejaService.updateMeja(currentTableId, 7, "TERSEDIA"));
    }

    @Test
    void testDeleteMejaSuccess() {
        UUID id = UUID.randomUUID();
        doNothing().when(mejaRepository).delete(id);

        mejaService.deleteMeja(id);

        verify(mejaRepository).delete(id);
        verify(eventPublisher).publishEvent(any(MejaDeletedEvent.class));
    }
}