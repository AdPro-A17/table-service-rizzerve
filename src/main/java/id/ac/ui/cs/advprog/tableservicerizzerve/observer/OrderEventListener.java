package id.ac.ui.cs.advprog.tableservicerizzerve.observer;

import id.ac.ui.cs.advprog.tableservicerizzerve.dto.OrderDetailsEvent;
import id.ac.ui.cs.advprog.tableservicerizzerve.service.MejaOrderUpdaterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class OrderEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderEventListener.class);
    private final MejaOrderUpdaterService mejaOrderUpdaterService;

    @Autowired
    public OrderEventListener(MejaOrderUpdaterService mejaOrderUpdaterService) {
        this.mejaOrderUpdaterService = mejaOrderUpdaterService;
    }

    @RabbitListener(queues = "${app.rabbitmq.queue.table-service.for-order-events}")
    public void handleOrderEvent(@Payload OrderDetailsEvent event) {
        LOGGER.info("TableService received OrderDetailsEvent: Type={}, OrderID={}, TableNum={}",
                event.getEventType(), event.getOrderId(), event.getTableNumber());
        try {
            switch (event.getEventType()) {
                case CREATED:
                    mejaOrderUpdaterService.handleOrderCreatedForTable(event);
                    break;
                case UPDATED:
                    if ("NEW".equals(event.getOrderStatus()) || "PROCESSING".equals(event.getOrderStatus())) {
                        mejaOrderUpdaterService.updateMejaWithActiveOrderDetails(event);
                    } else {
                        mejaOrderUpdaterService.clearActiveOrderFromMeja(event.getTableNumber());
                    }
                    break;
                case COMPLETED, CANCELLED:
                    mejaOrderUpdaterService.clearActiveOrderFromMeja(event.getTableNumber());
                    break;
                default:
                    LOGGER.warn("Unhandled OrderDetailsEvent type: {}", event.getEventType());
            }
        } catch (Exception e) {
            LOGGER.error("Error processing OrderDetailsEvent in TableService: " + event.toString(), e);
        }
    }
}