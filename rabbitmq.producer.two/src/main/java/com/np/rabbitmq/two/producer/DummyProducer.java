package com.np.rabbitmq.two.producer;

import com.np.rabbitmq.two.dto.DummyMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class DummyProducer {

    private static final Logger LOG = LoggerFactory.getLogger(DummyProducer.class);
    
    private final RabbitTemplate rabbitTemplate;

    public DummyProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMessage(DummyMessage dummyMessage) {
        // no need to convert data to JSON string; dummy message will be converted
        // behind the scene using the Jackson2JsonMessageConverter
        LOG.info("Sending {}...", dummyMessage);
        rabbitTemplate.convertAndSend("x.dummy", "", dummyMessage);
    }
}
