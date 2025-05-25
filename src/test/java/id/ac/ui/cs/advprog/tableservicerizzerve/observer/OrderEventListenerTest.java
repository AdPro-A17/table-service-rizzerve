package id.ac.ui.cs.advprog.tableservicerizzerve.observer;

import id.ac.ui.cs.advprog.tableservicerizzerve.dto.OrderDetailsEvent;
import id.ac.ui.cs.advprog.tableservicerizzerve.dto.OrderItemSummaryDto;
import id.ac.ui.cs.advprog.tableservicerizzerve.service.MejaOrderUpdaterService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderEventListenerTest {

    @Mock
    private MejaOrderUpdaterService mejaOrderUpdaterService;

    @InjectMocks
    private OrderEventListener orderEventListener;

    private OrderDetailsEvent createOrderEvent(OrderDetailsEvent.EventType eventType, String tableNumber, String orderStatus) {
        return OrderDetailsEvent.builder()
                .eventType(eventType)
                .orderId(UUID.randomUUID())
                .tableNumber(tableNumber)
                .orderStatus(orderStatus)
                .totalPrice(100.0)
                .items(Collections.singletonList(OrderItemSummaryDto.builder().menuItemName("Test Item").quantity(1).build()))
                .occurredAt(Instant.now())
                .build();
    }

    @Test
    void testHandleOrderCreatedEvent() {
        OrderDetailsEvent event = createOrderEvent(OrderDetailsEvent.EventType.CREATED, "101", "NEW");
        orderEventListener.handleOrderEvent(event);
        verify(mejaOrderUpdaterService, times(1)).handleOrderCreatedForTable(event);
        verifyNoMoreInteractions(mejaOrderUpdaterService);
    }

    @Test
    void testHandleOrderUpdatedEventNewStatus() {
        OrderDetailsEvent event = createOrderEvent(OrderDetailsEvent.EventType.UPDATED, "102", "NEW");
        orderEventListener.handleOrderEvent(event);
        verify(mejaOrderUpdaterService, times(1)).updateMejaWithActiveOrderDetails(event);
        verifyNoMoreInteractions(mejaOrderUpdaterService);
    }

    @Test
    void testHandleOrderUpdatedEventProcessingStatus() {
        OrderDetailsEvent event = createOrderEvent(OrderDetailsEvent.EventType.UPDATED, "103", "PROCESSING");
        orderEventListener.handleOrderEvent(event);
        verify(mejaOrderUpdaterService, times(1)).updateMejaWithActiveOrderDetails(event);
        verifyNoMoreInteractions(mejaOrderUpdaterService);
    }

    @Test
    void testHandleOrderUpdatedEventCompletedStatus() {
        OrderDetailsEvent event = createOrderEvent(OrderDetailsEvent.EventType.UPDATED, "104", "COMPLETED");
        orderEventListener.handleOrderEvent(event);
        verify(mejaOrderUpdaterService, times(1)).clearActiveOrderFromMeja(event.getTableNumber());
        verifyNoMoreInteractions(mejaOrderUpdaterService);
    }

    @Test
    void testHandleOrderCompletedEvent() {
        OrderDetailsEvent event = createOrderEvent(OrderDetailsEvent.EventType.COMPLETED, "105", "COMPLETED");
        orderEventListener.handleOrderEvent(event);
        verify(mejaOrderUpdaterService, times(1)).clearActiveOrderFromMeja(event.getTableNumber());
        verifyNoMoreInteractions(mejaOrderUpdaterService);
    }

    @Test
    void testHandleOrderCancelledEvent() {
        OrderDetailsEvent event = createOrderEvent(OrderDetailsEvent.EventType.CANCELLED, "106", "CANCELLED");
        orderEventListener.handleOrderEvent(event);
        verify(mejaOrderUpdaterService, times(1)).clearActiveOrderFromMeja(event.getTableNumber());
        verifyNoMoreInteractions(mejaOrderUpdaterService);
    }

    @Test
    void testHandleUnhandledEventType() {
        OrderDetailsEvent eventWithNullType = OrderDetailsEvent.builder().eventType(null).build();
        orderEventListener.handleOrderEvent(eventWithNullType);
        verifyNoInteractions(mejaOrderUpdaterService);
    }

    @Test
    void testServiceExceptionHandling() {
        OrderDetailsEvent event = createOrderEvent(OrderDetailsEvent.EventType.CREATED, "107", "NEW");
        doThrow(new RuntimeException("Service layer error")).when(mejaOrderUpdaterService).handleOrderCreatedForTable(any(OrderDetailsEvent.class));

        assertDoesNotThrow(() -> orderEventListener.handleOrderEvent(event));
        verify(mejaOrderUpdaterService, times(1)).handleOrderCreatedForTable(event);
    }

    @Test
    void testHandleOrderEventWithMockedUnknownEventType() {
        OrderDetailsEvent event = spy(OrderDetailsEvent.builder()
                .eventType(OrderDetailsEvent.EventType.CREATED)
                .orderId(UUID.randomUUID())
                .tableNumber("108")
                .orderStatus("UNKNOWN")
                .totalPrice(50.0)
                .items(Collections.emptyList())
                .occurredAt(Instant.now())
                .build());

        when(event.getEventType()).thenReturn(null);

        orderEventListener.handleOrderEvent(event);
        verifyNoInteractions(mejaOrderUpdaterService);
    }

    @Test
    void testSwitchDefaultCaseWithNullEventType() {
        OrderDetailsEvent event = OrderDetailsEvent.builder()
                .eventType(null)
                .orderId(UUID.randomUUID())
                .tableNumber("108")
                .orderStatus("UNKNOWN")
                .totalPrice(50.0)
                .items(Collections.emptyList())
                .occurredAt(Instant.now())
                .build();

        orderEventListener.handleOrderEvent(event);
        verifyNoInteractions(mejaOrderUpdaterService);
    }
}