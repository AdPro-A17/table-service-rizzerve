package id.ac.ui.cs.advprog.tableservicerizzerve.observer;

import id.ac.ui.cs.advprog.tableservicerizzerve.model.Meja;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MejaUpdatedEventTest {

    @Test
    void returnInjectedMeja() {
        Meja meja = new Meja(1, "TERSEDIA");
        Object source = new Object();

        MejaUpdatedEvent event = new MejaUpdatedEvent(source, meja);

        assertSame(source, event.getSource());
        assertSame(meja, event.getMeja());
    }
}