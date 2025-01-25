package com.np.rabbitmq.two.producer;

import com.np.rabbitmq.two.dto.DummyMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class MultiplePrefetchProducer {

    private static final Logger LOG = LoggerFactory.getLogger(MultiplePrefetchProducer.class);

    private final RabbitTemplate rabbitTemplate;

    public MultiplePrefetchProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void simulateTransaction() {
        for (int i = 0; i < 20_000; i++) {
            var message = new DummyMessage("Transaction " + i, i);
            LOG.info("Transaction: {}", message);
            rabbitTemplate.convertAndSend("x.transaction", "", message);
        }
    }

    public void simulateScheduler() {
        for (int i = 0; i < 200; i++) {
            var message = new DummyMessage("Scheduler " + i, i);
            LOG.info("Scheduler: {}", message);
            rabbitTemplate.convertAndSend("x.scheduler", "", message);
        }
    }
}
