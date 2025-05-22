package id.ac.ui.cs.advprog.tableservicerizzerve.observer;

import id.ac.ui.cs.advprog.tableservicerizzerve.dto.MejaEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MejaEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.routing-key.table.event.created}")
    private String rkCreated;
    @Value("${app.rabbitmq.routing-key.table.event.deleted}")
    private String rkDeleted;
    @Value("${app.rabbitmq.routing-key.table.event.updated.nomor}")
    private String rkUpdNomor;
    @Value("${app.rabbitmq.routing-key.table.event.updated.status}")
    private String rkUpdStatus;

    public void publish(MejaEvent evt) {
        String rk = switch (evt.getType()) {
            case CREATED -> rkCreated;
            case DELETED -> rkDeleted;
            case UPDATED_NOMOR -> rkUpdNomor;
            case UPDATED_STATUS -> rkUpdStatus;
        };
        rabbitTemplate.convertAndSend(rk, evt);
    }
}