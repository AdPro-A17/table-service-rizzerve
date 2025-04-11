package id.ac.ui.cs.advprog.tableservicerizzerve.observer;

import id.ac.ui.cs.advprog.tableservicerizzerve.model.Meja;
import org.junit.jupiter.api.Test;

class MejaEventListenerTest {

    @Test
    void testHandleMejaCreatedEvent() {
        Meja meja = new Meja(5, "TERSEDIA");
        MejaCreatedEvent event = new MejaCreatedEvent(this, meja);

        MejaEventListener listener = new MejaEventListener();
        listener.handleMejaCreatedEvent(event);
    }
}