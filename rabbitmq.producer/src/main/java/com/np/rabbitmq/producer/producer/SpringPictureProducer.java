package com.np.rabbitmq.producer.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.np.rabbitmq.producer.dto.Picture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;


@Component
public class SpringPictureProducer {

    private static final Logger LOG = LoggerFactory.getLogger(SpringPictureProducer.class);

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public SpringPictureProducer(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendMessage(Picture picture) throws JsonProcessingException {
        LOG.info("Sending picture {}", picture);
        var json = objectMapper.writeValueAsString(picture);
        rabbitTemplate.convertAndSend("x.spring.work", picture.type(), json);
    }
}
