package id.ac.ui.cs.advprog.tableservicerizzerve.service;

import id.ac.ui.cs.advprog.tableservicerizzerve.enums.MejaStatus;
import id.ac.ui.cs.advprog.tableservicerizzerve.exception.DuplicateNomorMejaException;
import id.ac.ui.cs.advprog.tableservicerizzerve.exception.InvalidMejaStatusException;
import id.ac.ui.cs.advprog.tableservicerizzerve.exception.InvalidNomorMejaException;
import id.ac.ui.cs.advprog.tableservicerizzerve.exception.MejaNotFoundException;
import id.ac.ui.cs.advprog.tableservicerizzerve.model.Meja;
import id.ac.ui.cs.advprog.tableservicerizzerve.observer.MejaCreatedEvent;
import id.ac.ui.cs.advprog.tableservicerizzerve.observer.MejaDeletedEvent;
import id.ac.ui.cs.advprog.tableservicerizzerve.observer.MejaUpdatedEvent;
import id.ac.ui.cs.advprog.tableservicerizzerve.repository.MejaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MejaServiceTest {

    private MejaRepository mejaRepository;
    private ApplicationEventPublisher eventPublisher;
    private MejaServiceImpl mejaService;

    @BeforeEach
    void setUp() {
        mejaRepository = mock(MejaRepository.class);
        eventPublisher = mock(ApplicationEventPublisher.class);
        mejaService    = new MejaServiceImpl(mejaRepository, eventPublisher);
    }

    @Test
    void testCreateMejaSuccess() {
        when(mejaRepository.findByNomorMeja(5)).thenReturn(Optional.empty());
        when(mejaRepository.save(any(Meja.class))).thenAnswer(i -> i.getArgument(0));

        Meja result = mejaService.createMeja(5, MejaStatus.TERSEDIA.getValue());

        assertNotNull(result.getId());
        assertEquals(5, result.getNomorMeja());
        assertEquals(MejaStatus.TERSEDIA.getValue(), result.getStatus());
        verify(mejaRepository).findByNomorMeja(5);
        verify(mejaRepository).save(any(Meja.class));
        verify(eventPublisher).publishEvent(any(MejaCreatedEvent.class));
    }

    @Test
    void testCreateMejaWithNullStatusDefaultsToTersedia() {
        when(mejaRepository.findByNomorMeja(1)).thenReturn(Optional.empty());
        when(mejaRepository.save(any(Meja.class))).thenAnswer(i -> i.getArgument(0));

        Meja result = mejaService.createMeja(1, null);

        assertEquals(MejaStatus.TERSEDIA.getValue(), result.getStatus());
    }

    @Test
    void testCreateMejaInvalidNomorShouldThrow() {
        assertThrows(InvalidNomorMejaException.class, () -> mejaService.createMeja(0, MejaStatus.TERSEDIA.getValue()));
    }

    @Test
    void testCreateMejaInvalidStatusShouldThrow() {
        when(mejaRepository.findByNomorMeja(1)).thenReturn(Optional.empty());
        assertThrows(InvalidMejaStatusException.class, () -> mejaService.createMeja(1, "BAD_STATUS"));
    }

    @Test
    void testCreateMejaDuplicateNomorShouldThrow() {
        when(mejaRepository.findByNomorMeja(5)).thenReturn(Optional.of(new Meja(5, MejaStatus.TERSEDIA.getValue())));
        assertThrows(DuplicateNomorMejaException.class, () -> mejaService.createMeja(5, MejaStatus.TERSEDIA.getValue()));
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
    void testUpdateMejaSuccess() {
        UUID id = UUID.randomUUID();
        Meja stored = new Meja(1, MejaStatus.TERSEDIA.getValue());
        stored.setId(id);
        when(mejaRepository.findById(id)).thenReturn(Optional.of(stored));
        when(mejaRepository.findByNomorMeja(9)).thenReturn(Optional.empty());
        when(mejaRepository.save(any(Meja.class))).thenAnswer(i -> i.getArgument(0));

        Meja result = mejaService.updateMeja(id, 9, MejaStatus.TERPAKAI.getValue());

        assertEquals(9, result.getNomorMeja());
        assertEquals(MejaStatus.TERPAKAI.getValue(), result.getStatus());
        verify(eventPublisher).publishEvent(any(MejaUpdatedEvent.class));
    }

    @Test
    void testUpdateMejaDuplicateNomorThrows() {
        UUID id = UUID.randomUUID();
        Meja stored   = new Meja(3,  MejaStatus.TERSEDIA.getValue());
        Meja conflict = new Meja(7,  MejaStatus.TERSEDIA.getValue());
        stored.setId(id);
        conflict.setId(UUID.randomUUID());
        when(mejaRepository.findById(id)).thenReturn(Optional.of(stored));
        when(mejaRepository.findByNomorMeja(7)).thenReturn(Optional.of(conflict));

        assertThrows(DuplicateNomorMejaException.class, () -> mejaService.updateMeja(id, 7, MejaStatus.TERSEDIA.getValue()));
    }

    @Test
    void testUpdateMejaNoConflictWhenSameNumber() {
        UUID id = UUID.randomUUID();
        Meja stored = new Meja(5, MejaStatus.TERSEDIA.getValue());
        stored.setId(id);

        when(mejaRepository.findById(id)).thenReturn(Optional.of(stored));
        when(mejaRepository.findByNomorMeja(5)).thenReturn(Optional.of(stored));
        when(mejaRepository.save(any(Meja.class))).thenAnswer(i -> i.getArgument(0));

        Meja result = mejaService.updateMeja(id, 5, MejaStatus.TERPAKAI.getValue());

        assertEquals(5, result.getNomorMeja());
        assertEquals(MejaStatus.TERPAKAI.getValue(), result.getStatus());
        verify(mejaRepository).findByNomorMeja(5);
    }

    @Test
    void testUpdateMejaNotFoundThrows() {
        UUID id = UUID.randomUUID();
        when(mejaRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(MejaNotFoundException.class, () -> mejaService.updateMeja(id, 5, MejaStatus.TERSEDIA.getValue()));
    }

    @Test
    void testUpdateMejaInvalidNomorThrows() {
        UUID id = UUID.randomUUID();
        Meja stored = new Meja(1, MejaStatus.TERSEDIA.getValue());
        stored.setId(id);
        when(mejaRepository.findById(id)).thenReturn(Optional.of(stored));

        assertThrows(InvalidNomorMejaException.class, () -> mejaService.updateMeja(id, 0, MejaStatus.TERSEDIA.getValue()));
    }

    @Test
    void testDeleteMejaSuccess() {
        UUID id = UUID.randomUUID();
        Meja existing = new Meja(4, MejaStatus.TERSEDIA.getValue());
        existing.setId(id);
        when(mejaRepository.existsById(id)).thenReturn(true);
        when(mejaRepository.findById(id)).thenReturn(Optional.of(existing));
        doNothing().when(mejaRepository).deleteById(id);

        mejaService.deleteMeja(id);

        verify(mejaRepository).existsById(id);
        verify(mejaRepository).deleteById(id);
        verify(eventPublisher).publishEvent(any(MejaDeletedEvent.class));
    }

    @Test
    void testDeleteMejaNotFoundThrows() {
        UUID id = UUID.randomUUID();
        when(mejaRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(MejaNotFoundException.class, () -> mejaService.deleteMeja(id));
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
    }

    @Test
    void testFindAllMejaEmpty() {
        when(mejaRepository.findAll()).thenReturn(List.of());

        List<Meja> result = mejaService.findAllMeja();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mejaRepository).findAll();
    }
}