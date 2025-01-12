package com.np.rabbitmq.consumer.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.np.rabbitmq.consumer.dto.Picture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PictureTwoConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(PictureTwoConsumer.class);

    private final ObjectMapper objectMapper;

    public PictureTwoConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = {"q.picture2.image", "q.picture2.vector", "q.picture2.mobile", "q.picture2.log"})
    public void listen(Message message) throws JsonProcessingException {
        var picture = objectMapper.readValue(new String(message.getBody()), Picture.class);
        LOG.info("Consumed {} with routing key {}", picture, message.getMessageProperties().getReceivedRoutingKey());
    }
}
