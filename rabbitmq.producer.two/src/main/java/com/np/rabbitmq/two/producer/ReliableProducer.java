package com.np.rabbitmq.two.producer;

import com.np.rabbitmq.two.dto.DummyMessage;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ReliableProducer {


    private static final Logger LOG = LoggerFactory.getLogger(ReliableProducer.class);

    private final RabbitTemplate rabbitTemplate;

    public ReliableProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostConstruct // it's important that this is executed after the bean is injected
    private void registerCallback() {
        // exchange check
        rabbitTemplate.setConfirmCallback(((correlation, ack, reason) -> {
            if (correlation == null) {
                return;
            }

            if (ack) {
                LOG.info("Message with correlation {} is published", correlation.getId());
            } else {
                LOG.warn("Invalid exchange. Message with correlation {} is NOT published", correlation.getId());
            }
        }));

        rabbitTemplate.setReturnsCallback(returned -> {
            LOG.info("Return callback");

            if (returned.getReplyText() != null && returned.getReplyText().equalsIgnoreCase("NO_ROUTE")) {
                var id = returned.getMessage()
                        .getMessageProperties()
                        .getHeader("spring_returned_message_correlation")
                        .toString();
                LOG.warn("Invalid routing key for id: {}", id);
            }
        });
    }

    public void sendDummyMessageWithInvalidRoutingKey(DummyMessage message) {
        var correlationData = new CorrelationData(UUID.randomUUID().toString()); // to track message
        // other errors
        // fanout exchange without binding
        // topic exchange with no matching routing key
        // sending to non-existent exchange
        rabbitTemplate.convertAndSend("x.dummy", "invalidRoutingKey", message, correlationData);
    }

    public void sendDummyMessageToNonExistentExchange(DummyMessage message) {
        var correlationData = new CorrelationData(UUID.randomUUID().toString()); // to track message
        // other errors
        // fanout exchange without binding
        // topic exchange with no matching routing key
        // sending to non-existent exchange
        rabbitTemplate.convertAndSend("InvalidExchange", "", message, correlationData);
    }
}
