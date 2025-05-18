package id.ac.ui.cs.advprog.tableservicerizzerve.observer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MejaEventPublisherTest {

    private RabbitTemplate rabbitTemplate;
    private MejaEventPublisher publisher;

    @BeforeEach
    void setUp() {
        rabbitTemplate = mock(RabbitTemplate.class);
        publisher = new MejaEventPublisher(rabbitTemplate);

        ReflectionTestUtils.setField(publisher, "rkCreated", "table.event.created");
        ReflectionTestUtils.setField(publisher, "rkDeleted", "table.event.deleted");
        ReflectionTestUtils.setField(publisher, "rkUpdNomor", "table.event.updated.nomor");
        ReflectionTestUtils.setField(publisher, "rkUpdStatus", "table.event.updated.status");
    }

    @Test
    void publishSendsCreatedToCorrectKey() {
        verifyRouting(MejaEvent.Type.CREATED, "table.event.created");
    }

    @Test
    void publishSendsDeletedToCorrectKey() {
        verifyRouting(MejaEvent.Type.DELETED, "table.event.deleted");
    }

    @Test
    void publishSendsUpdatedNomorToCorrectKey() {
        verifyRouting(MejaEvent.Type.UPDATED_NOMOR, "table.event.updated.nomor");
    }

    @Test
    void publishSendsUpdatedStatusToCorrectKey() {
        verifyRouting(MejaEvent.Type.UPDATED_STATUS, "table.event.updated.status");
    }

    private void verifyRouting(MejaEvent.Type type, String expectedKey) {
        MejaEvent evt = new MejaEvent(type, UUID.randomUUID(), 1, null, "TERSEDIA", Instant.now());

        publisher.publish(evt);

        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<MejaEvent> evtCaptor = ArgumentCaptor.forClass(MejaEvent.class);

        verify(rabbitTemplate).convertAndSend(keyCaptor.capture(), evtCaptor.capture());
        assertEquals(expectedKey, keyCaptor.getValue());
        assertEquals(evt, evtCaptor.getValue());
    }
}