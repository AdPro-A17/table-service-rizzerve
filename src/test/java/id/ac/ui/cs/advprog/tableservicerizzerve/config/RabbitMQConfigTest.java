package id.ac.ui.cs.advprog.tableservicerizzerve.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {RabbitMQConfig.class, RabbitMQConfigTest.TestConfig.class})
@TestPropertySource(properties = {
        "app.rabbitmq.exchange.table-events=test.table.events.exchange.publisher",
        "app.rabbitmq.exchange.order-events=test.order.events.exchange.subscriber",
        "app.rabbitmq.queue.table-service.for-order-events=test.table-service.order.queue",
        "app.rabbitmq.routing-key.order.event.created=test.order.created",
        "app.rabbitmq.routing-key.order.event.updated=test.order.updated",
        "app.rabbitmq.routing-key.order.event.completed=test.order.completed"
})
class RabbitMQConfigTest {
    @Configuration
    static class TestConfig {
        @Bean
        ConnectionFactory connectionFactory() {
            return Mockito.mock(ConnectionFactory.class);
        }
    }

    @Autowired
    private ApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageConverter jsonMessageConverter;

    @Autowired
    @Qualifier("tableEventsPublisherExchange")
    private TopicExchange tableEventsPublisherExchange;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    @Qualifier("orderEventsSubscriberExchange")
    private TopicExchange orderEventsSubscriberExchange;

    @Autowired
    @Qualifier("tableServiceOrderEventsQueue")
    private Queue tableServiceOrderEventsQueue;

    @Autowired
    @Qualifier("orderCreatedBindingToTableService")
    private Binding orderCreatedBinding;

    @Autowired
    @Qualifier("orderUpdatedBindingToTableService")
    private Binding orderUpdatedBinding;

    @Autowired
    @Qualifier("orderCompletedBindingToTableService")
    private Binding orderCompletedBinding;


    @Test
    void objectMapperBeanIsConfigured() {
        assertNotNull(objectMapper, "ObjectMapper bean should be created.");
        assertTrue(objectMapper.canSerialize(java.time.Instant.class), "ObjectMapper should be able to serialize Instant.");
    }

    @Test
    void jsonMessageConverterBeanIsConfigured() {
        assertNotNull(jsonMessageConverter, "jsonMessageConverter bean should be created.");
        assertTrue(jsonMessageConverter instanceof Jackson2JsonMessageConverter, "Converter should be Jackson2JsonMessageConverter.");
    }

    @Test
    void publisherBeansAreConfiguredCorrectly() {
        assertNotNull(tableEventsPublisherExchange, "tableEventsPublisherExchange bean should be created.");
        assertEquals("test.table.events.exchange.publisher", tableEventsPublisherExchange.getName(), "Publisher exchange name should match property.");
        assertTrue(tableEventsPublisherExchange.isDurable(), "Publisher exchange should be durable.");
        assertFalse(tableEventsPublisherExchange.isAutoDelete(), "Publisher exchange should not auto-delete.");

        assertNotNull(rabbitTemplate, "rabbitTemplate bean should be created.");
        assertSame(jsonMessageConverter, rabbitTemplate.getMessageConverter(), "RabbitTemplate should use the configured jsonMessageConverter.");
        assertEquals("test.table.events.exchange.publisher", rabbitTemplate.getExchange(), "RabbitTemplate default exchange should be set.");
    }

    @Test
    void subscriberBeansAreConfiguredCorrectly() {
        assertNotNull(orderEventsSubscriberExchange, "orderEventsSubscriberExchange bean should be created.");
        assertEquals("test.order.events.exchange.subscriber", orderEventsSubscriberExchange.getName(), "Subscriber exchange name should match property.");
        assertTrue(orderEventsSubscriberExchange.isDurable(), "Subscriber exchange should be durable.");
        assertFalse(orderEventsSubscriberExchange.isAutoDelete(), "Subscriber exchange should not auto-delete.");

        assertNotNull(tableServiceOrderEventsQueue, "tableServiceOrderEventsQueue bean should be created.");
        assertEquals("test.table-service.order.queue", tableServiceOrderEventsQueue.getName(), "Queue name should match property.");
        assertTrue(tableServiceOrderEventsQueue.isDurable(), "Queue should be durable.");
    }

    @Test
    void bindingBeansAreConfiguredCorrectly() {
        assertNotNull(orderCreatedBinding, "orderCreatedBinding bean should be created.");
        assertEquals("test.table-service.order.queue", orderCreatedBinding.getDestination(), "Binding destination should be the queue.");
        assertEquals("test.order.events.exchange.subscriber", orderCreatedBinding.getExchange(), "Binding exchange should be the subscriber exchange.");
        assertEquals("test.order.created", orderCreatedBinding.getRoutingKey(), "Binding routing key for created should match property.");

        assertNotNull(orderUpdatedBinding, "orderUpdatedBinding bean should be created.");
        assertEquals("test.table-service.order.queue", orderUpdatedBinding.getDestination());
        assertEquals("test.order.events.exchange.subscriber", orderUpdatedBinding.getExchange());
        assertEquals("test.order.updated", orderUpdatedBinding.getRoutingKey());

        assertNotNull(orderCompletedBinding, "orderCompletedBinding bean should be created.");
        assertEquals("test.table-service.order.queue", orderCompletedBinding.getDestination());
        assertEquals("test.order.events.exchange.subscriber", orderCompletedBinding.getExchange());
        assertEquals("test.order.completed", orderCompletedBinding.getRoutingKey());
    }

    @Test
    void ensureAllExpectedBeansArePresent() {
        assertNotNull(context.getBean("objectMapper", ObjectMapper.class));
        assertNotNull(context.getBean("jsonMessageConverter", MessageConverter.class));
        assertNotNull(context.getBean("tableEventsPublisherExchange", TopicExchange.class));
        assertNotNull(context.getBean("rabbitTemplate", RabbitTemplate.class));
        assertNotNull(context.getBean("orderEventsSubscriberExchange", TopicExchange.class));
        assertNotNull(context.getBean("tableServiceOrderEventsQueue", Queue.class));
        assertNotNull(context.getBean("orderCreatedBindingToTableService", Binding.class));
        assertNotNull(context.getBean("orderUpdatedBindingToTableService", Binding.class));
        assertNotNull(context.getBean("orderCompletedBindingToTableService", Binding.class));
    }
}