package id.ac.ui.cs.advprog.tableservicerizzerve.repository;

import id.ac.ui.cs.advprog.tableservicerizzerve.model.Meja;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

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

    @Test
    void testFindAllMeja() {
        Meja meja1 = new Meja(1, "TERSEDIA");
        Meja meja2 = new Meja(2, "TERSEDIA");

        mejaRepository.save(meja1);
        mejaRepository.save(meja2);

        List<Meja> result = mejaRepository.findAll();

        assertEquals(2, result.size());
        assertTrue(result.contains(meja1));
        assertTrue(result.contains(meja2));
    }
}