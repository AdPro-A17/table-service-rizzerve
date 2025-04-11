package id.ac.ui.cs.advprog.tableservicerizzerve.observer;

import id.ac.ui.cs.advprog.tableservicerizzerve.model.Meja;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MejaCreatedEventTest {

    @Test
    void testGetMeja() {
        Meja meja = new Meja(1, "TERSEDIA");
        MejaCreatedEvent event = new MejaCreatedEvent(this, meja);

        assertEquals(meja, event.getMeja());
    }
}