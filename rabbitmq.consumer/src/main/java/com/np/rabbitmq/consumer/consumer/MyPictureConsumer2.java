package com.np.rabbitmq.consumer.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.np.rabbitmq.consumer.dto.Picture;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MyPictureConsumer2 {

    private static final Logger LOG = LoggerFactory.getLogger(MyPictureConsumer2.class);

    private final ObjectMapper objectMapper;

    public MyPictureConsumer2(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

//    @RabbitListener(queues = "q.mypicture.image")
    public void listen(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        // channel - a tunnel between RMQ and Java
        // delivery tag - a unique identifier for each message
        var picture = objectMapper.readValue(message, Picture.class);
        if (picture.size() > 9000) {
            channel.basicReject(tag, false); // reject
        }
        LOG.info("Consumed {}...", picture);
        channel.basicAck(tag, false); // acknowledge (so, it's not processed again)
    }
}
