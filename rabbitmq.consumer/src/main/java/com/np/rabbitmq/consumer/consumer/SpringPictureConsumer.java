package com.np.rabbitmq.consumer.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.np.rabbitmq.consumer.dto.Picture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SpringPictureConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(SpringPictureConsumer.class);
    private final ObjectMapper objectMapper;

    public SpringPictureConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

//    @RabbitListener(queues = "q.spring.image.work")
    public void listenImage(String message) throws IOException {
        var picture = objectMapper.readValue(message, Picture.class);
        LOG.info("Consuming image {}...", picture);

        if (picture.size() > 9000) {
            throw new IOException(String.format("Image %s size %d is too large", picture.name(), picture.size()));
        }

        LOG.info("Processing image {} and creating its thumbnail...", picture.name());
    }

//    @RabbitListener(queues = "q.spring.vector.work")
    public void listenVector(String message) throws IOException {
        var picture = objectMapper.readValue(message, Picture.class);
        LOG.info("Consuming vector {}...", picture);
        LOG.info("Processing vector {} and creating its thumbnail...", picture.name());
    }
}
