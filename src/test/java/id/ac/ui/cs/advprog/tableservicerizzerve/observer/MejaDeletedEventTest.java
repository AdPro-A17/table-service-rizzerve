package id.ac.ui.cs.advprog.tableservicerizzerve.observer;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MejaDeletedEventTest {

    @Test
    void returnInjectedId() {
        UUID id     = UUID.randomUUID();
        Object src  = new Object();

        MejaDeletedEvent event = new MejaDeletedEvent(src, id);

        assertSame(src, event.getSource());
        assertEquals(id, event.getMejaId());
    }
}