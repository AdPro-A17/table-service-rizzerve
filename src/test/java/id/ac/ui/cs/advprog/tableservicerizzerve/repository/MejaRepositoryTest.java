package id.ac.ui.cs.advprog.tableservicerizzerve.repository;

import id.ac.ui.cs.advprog.tableservicerizzerve.model.Meja;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MejaRepositoryTest {

    private MejaRepository mejaRepository;

    @BeforeEach
    void setUp() {
        mejaRepository = new MejaRepository();
    }

    @Test
    void testSaveMeja() {
        Meja meja = new Meja(5, "TERSEDIA");
        Meja savedMeja = mejaRepository.save(meja);

        assertNotNull(savedMeja);
        assertEquals(meja.getId(), savedMeja.getId());
        assertEquals(5, savedMeja.getNomorMeja());
        assertEquals("TERSEDIA", savedMeja.getStatus());
    }
}