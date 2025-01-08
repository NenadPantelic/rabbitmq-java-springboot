package com.np.rabbitmq.producer.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class FixedRateProducer {

    private static final Logger LOG = LoggerFactory.getLogger(FixedRateProducer.class);
    private final RabbitTemplate rabbitTemplate;
    private int counter = 0;

    public FixedRateProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Scheduled(fixedRate = 500)
    public void sendMessage() {
        counter++;
        LOG.info("Sending {}...", counter);
        rabbitTemplate.convertAndSend("np.fixedrate", "Fixed rate: " + counter);
    }
}
