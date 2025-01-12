package com.np.rabbitmq.consumer.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.np.rabbitmq.consumer.dto.Picture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PictureVectorConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(PictureVectorConsumer.class);

    private final ObjectMapper objectMapper;

    public PictureVectorConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "q.picture.vector")
    public void listen(String message) throws JsonMappingException, JsonProcessingException {
        var picture = objectMapper.readValue(message, Picture.class);
        LOG.info("Consumed {}...", picture);
    }
}
