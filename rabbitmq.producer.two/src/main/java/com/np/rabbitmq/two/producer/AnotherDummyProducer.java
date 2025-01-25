package com.np.rabbitmq.two.producer;

import com.np.rabbitmq.two.dto.DummyMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class AnotherDummyProducer {

    private static final Logger LOG = LoggerFactory.getLogger(DummyProducer.class);

    private final RabbitTemplate rabbitTemplate;

    public AnotherDummyProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMessage(DummyMessage dummyMessage) {
        LOG.info("Sending {}...", dummyMessage);
        rabbitTemplate.convertAndSend("x.another-dummy", "", dummyMessage);
    }
}
