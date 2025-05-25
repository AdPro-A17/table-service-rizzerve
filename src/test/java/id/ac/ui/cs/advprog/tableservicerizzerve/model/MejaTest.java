package id.ac.ui.cs.advprog.tableservicerizzerve.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MejaTest {

    private Meja meja;

    @BeforeEach
    void setUp() {
        this.meja = new Meja(10, "tersedia");
    }

    @Test
    void testGetIdNotNull() {
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

    @Test
    void testSetNomorMeja() {
        this.meja.setNomorMeja(15);
        assertEquals(15, this.meja.getNomorMeja());
    }

    @Test
    void testSetId() {
        UUID newId = UUID.randomUUID();
        this.meja.setId(newId);
        assertEquals(newId, this.meja.getId());
    }

    @Test
    void testGetActiveOrderId() {
        assertNull(this.meja.getActiveOrderId());
    }

    @Test
    void testSetActiveOrderId() {
        UUID orderId = UUID.randomUUID();
        this.meja.setActiveOrderId(orderId);
        assertEquals(orderId, this.meja.getActiveOrderId());
    }

    @Test
    void testGetActiveOrderStatus() {
        assertNull(this.meja.getActiveOrderStatus());
    }

    @Test
    void testSetActiveOrderStatus() {
        this.meja.setActiveOrderStatus("PENDING");
        assertEquals("PENDING", this.meja.getActiveOrderStatus());
    }

    @Test
    void testGetActiveOrderTotalPrice() {
        assertNull(this.meja.getActiveOrderTotalPrice());
    }

    @Test
    void testSetActiveOrderTotalPrice() {
        this.meja.setActiveOrderTotalPrice(50000.0);
        assertEquals(50000.0, this.meja.getActiveOrderTotalPrice());
    }

    @Test
    void testGetActiveOrderItemsJson() {
        assertNull(this.meja.getActiveOrderItemsJson());
    }

    @Test
    void testSetActiveOrderItemsJson() {
        String json = "{\"items\": []}";
        this.meja.setActiveOrderItemsJson(json);
        assertEquals(json, this.meja.getActiveOrderItemsJson());
    }

    @Test
    void testNoArgsConstructor() {
        Meja emptyMeja = new Meja();
        assertNotNull(emptyMeja);
        assertNull(emptyMeja.getId());
        assertEquals(0, emptyMeja.getNomorMeja());
        assertNull(emptyMeja.getActiveOrderId());
        assertNull(emptyMeja.getActiveOrderStatus());
        assertNull(emptyMeja.getActiveOrderTotalPrice());
        assertNull(emptyMeja.getActiveOrderItemsJson());
    }
}