package id.ac.ui.cs.advprog.tableservicerizzerve.service;

import id.ac.ui.cs.advprog.tableservicerizzerve.model.Meja;
import id.ac.ui.cs.advprog.tableservicerizzerve.observer.MejaCreatedEvent;
import id.ac.ui.cs.advprog.tableservicerizzerve.repository.MejaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

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
    void testCreateMeja_Success() {
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
    void testCreateMeja_InvalidNomorMeja_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> mejaService.createMeja(0, "TERSEDIA"));
    }

    @Test
    void testCreateMeja_InvalidStatus_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> mejaService.createMeja(1, "INVALID_STATUS"));
    }

    @Test
    void testFindAllMeja_ReturnsListOfMeja() {
        Meja meja1 = new Meja(1, "TERSEDIA");
        Meja meja2 = new Meja(2, "TERSEDIA");

        when(mejaRepository.findAll()).thenReturn(List.of(meja1, meja2));

        List<Meja> result = mejaService.findAllMeja();

        assertEquals(2, result.size());
        assertTrue(result.contains(meja1));
        assertTrue(result.contains(meja2));

        verify(mejaRepository, times(1)).findAll();
    }
}