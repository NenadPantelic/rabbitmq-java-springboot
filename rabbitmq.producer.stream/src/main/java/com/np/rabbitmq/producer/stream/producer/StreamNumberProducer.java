package com.np.rabbitmq.producer.stream.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class StreamNumberProducer {

    private static final Logger LOG = LoggerFactory.getLogger(StreamNumberProducer.class);

    private final RabbitTemplate rabbitTemplate;

    public StreamNumberProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendNumbers(int start, int end) {
        for (int i = start; i < end; i++) {
            var payload = "Number " + i;
            LOG.info("Send '{}'", payload);
            rabbitTemplate.convertAndSend("x.number", "", payload);
        }
    }
}
