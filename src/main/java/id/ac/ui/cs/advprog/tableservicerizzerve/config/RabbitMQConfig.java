package id.ac.ui.cs.advprog.tableservicerizzerve.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class RabbitMQConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Value("${app.rabbitmq.exchange.table-events}")
    private String tableEventsExchangeName;

    @Bean
    public TopicExchange tableEventsPublisherExchange() {
        return new TopicExchange(tableEventsExchangeName, true, false);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        template.setExchange(tableEventsExchangeName);
        return template;
    }

    @Value("${app.rabbitmq.exchange.order-events}")
    private String orderEventsExchangeName;

    @Value("${app.rabbitmq.queue.table-service.for-order-events}")
    private String tableServiceOrderEventsQueueName;

    @Value("${app.rabbitmq.routing-key.order.event.created}")
    private String rkOrderCreated;
    @Value("${app.rabbitmq.routing-key.order.event.updated}")
    private String rkOrderUpdated;
    @Value("${app.rabbitmq.routing-key.order.event.completed}")
    private String rkOrderCompleted;

    @Bean
    public TopicExchange orderEventsSubscriberExchange() {
        return new TopicExchange(orderEventsExchangeName, true, false);
    }

    @Bean
    public Queue tableServiceOrderEventsQueue() {
        return new Queue(tableServiceOrderEventsQueueName, true);
    }

    @Bean
    public Binding orderCreatedBindingToTableService( @Qualifier("tableServiceOrderEventsQueue") Queue queue, @Qualifier("orderEventsSubscriberExchange") TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(rkOrderCreated);
    }

    @Bean
    public Binding orderUpdatedBindingToTableService( @Qualifier("tableServiceOrderEventsQueue") Queue queue,  @Qualifier("orderEventsSubscriberExchange") TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(rkOrderUpdated);
    }

    @Bean
    public Binding orderCompletedBindingToTableService( @Qualifier("tableServiceOrderEventsQueue") Queue queue, @Qualifier("orderEventsSubscriberExchange") TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(rkOrderCompleted);
    }
}