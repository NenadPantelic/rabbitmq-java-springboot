package com.np.rabbitmq.consumer.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.np.rabbitmq.consumer.dto.Picture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MyPictureConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(MyPictureConsumer.class);

    private final ObjectMapper objectMapper;

    public MyPictureConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

//    @RabbitListener(queues = "q.mypicture.image")
    public void listen(String message) throws JsonMappingException, JsonProcessingException {
        var picture = objectMapper.readValue(message, Picture.class);
        if (picture.size() > 9000) {
            throw new AmqpRejectAndDontRequeueException("Picture size too large");
        }
        LOG.info("Consumed {}...", picture);
    }
}
