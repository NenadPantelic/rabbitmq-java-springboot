package com.np.rabbitmq.two.producer;

import com.np.rabbitmq.two.dto.DummyMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class SingleActiveProducer {

    private static final Logger LOG = LoggerFactory.getLogger(InvoiceProducer.class);

    private final RabbitTemplate rabbitTemplate;

    public SingleActiveProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendDummy() {
        for (int i = 0; i < 10_000; i++) {
            var message = new DummyMessage("content" + i, i);
            LOG.info("Sending {}", message);
            rabbitTemplate.convertAndSend("x.single", "", message);
        }
    }
}
