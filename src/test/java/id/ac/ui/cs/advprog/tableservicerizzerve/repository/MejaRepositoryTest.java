package id.ac.ui.cs.advprog.tableservicerizzerve.repository;

import id.ac.ui.cs.advprog.tableservicerizzerve.enums.MejaStatus;
import id.ac.ui.cs.advprog.tableservicerizzerve.model.Meja;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class MejaRepositoryTest {

    @Autowired
    private MejaRepository mejaRepository;

    @Test
    void testSaveMeja() {
        Meja meja = new Meja(5, "TERSEDIA");
        Meja saved = mejaRepository.save(meja);

        assertNotNull(saved.getId());
        assertEquals(5, saved.getNomorMeja());
        assertEquals("TERSEDIA", saved.getStatus());
    }

    @Test
    void testFindAllMeja() {
        Meja m1 = new Meja(1, "TERSEDIA");
        Meja m2 = new Meja(2, "TERSEDIA");
        mejaRepository.save(m1);
        mejaRepository.save(m2);

        List<Meja> all = mejaRepository.findAll();
        assertEquals(2, all.size());
        assertTrue(all.stream().anyMatch(m -> m.getNomorMeja() == 1));
        assertTrue(all.stream().anyMatch(m -> m.getNomorMeja() == 2));
    }

    @Test
    void testFindById() {
        Meja meja = new Meja(6, "TERSEDIA");
        Meja saved = mejaRepository.save(meja);

        Optional<Meja> found = mejaRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
        assertEquals(6, found.get().getNomorMeja());
        assertEquals("TERSEDIA", found.get().getStatus());
    }

    @Test
    void testFindByNomorMeja() {
        Meja a = new Meja(10, "TERSEDIA");
        Meja b = new Meja(11, "TERSEDIA");
        mejaRepository.save(a);
        mejaRepository.save(b);

        Optional<Meja> found = mejaRepository.findByNomorMeja(11);
        assertTrue(found.isPresent());
        assertEquals(11, found.get().getNomorMeja());
    }

    @Test
    void testUpdateMeja() {
        Meja meja = new Meja(3, "TERSEDIA");
        Meja saved = mejaRepository.save(meja);

        saved.setNomorMeja(8);
        saved.setStatus(MejaStatus.TERPAKAI);
        Meja updated = mejaRepository.save(saved);

        assertEquals(8, updated.getNomorMeja());
        assertEquals("TERPAKAI", updated.getStatus());
    }

    @Test
    void testDeleteMeja() {
        Meja meja = new Meja(4, "TERSEDIA");
        Meja saved = mejaRepository.save(meja);
        mejaRepository.deleteById(saved.getId());

        assertTrue(mejaRepository.findAll().isEmpty());
    }
}