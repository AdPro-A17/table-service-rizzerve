package id.ac.ui.cs.advprog.tableservicerizzerve.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MejaTest {

    private Meja meja;

    @BeforeEach
    void setUp() {
        this.meja = new Meja(10, "tersedia");
    }

    @Test
    void testGetId_NotNull() {
        assertNotNull(this.meja.getId());
    }

    @Test
    void testGetNomorMeja() {
        assertEquals(10, this.meja.getNomorMeja());
    }

    @Test
    void testGetStatus() {
        assertEquals("TERSEDIA", this.meja.getStatus());
    }
}