package id.ac.ui.cs.advprog.tableservicerizzerve.config;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = { RabbitMQConfig.class, RabbitMQConfigTest.MockCF.class })
@TestPropertySource(properties = "app.rabbitmq.exchange.table-events=test.exchange")
class RabbitMQConfigTest {

    @Configuration
    static class MockCF {
        @Bean ConnectionFactory connectionFactory() {
            return Mockito.mock(ConnectionFactory.class);
        }
    }

    @Autowired private TopicExchange exchange;
    @Autowired private RabbitTemplate rabbitTemplate;
    @Autowired private Jackson2JsonMessageConverter jsonConverter;

    @Test
    void beansAreConfiguredCorrectly() {
        assertEquals("test.exchange", exchange.getName());
        assertSame(jsonConverter, rabbitTemplate.getMessageConverter());
        assertEquals("test.exchange", rabbitTemplate.getExchange());
    }
}