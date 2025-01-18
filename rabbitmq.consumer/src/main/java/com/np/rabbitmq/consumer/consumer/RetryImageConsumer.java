package com.np.rabbitmq.consumer.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.np.rabbitmq.consumer.dto.Picture;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RetryImageConsumer {

    private static final String DEAD_EXCHANGE_NAME = "x.guideline.dead";
    private static final Logger LOG = LoggerFactory.getLogger(RetryImageConsumer.class);


    private final DlxProcessingErrorHandler dlxProcessingErrorHandler;
    private final ObjectMapper objectMapper;

    public RetryImageConsumer(ObjectMapper objectMapper) {
        this.dlxProcessingErrorHandler = new DlxProcessingErrorHandler(DEAD_EXCHANGE_NAME);
        this.objectMapper = objectMapper;
    }

//    @RabbitListener(queues = "q.guideline.image.work")
    public void listen(Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        try {
            var picture = objectMapper.readValue(message.getBody(), Picture.class);
            if (picture.size() > 9000) {
                // throw an exception, DLX handler for retry mechanism
                throw new IOException("Size too large!");
            }

            LOG.info("Creating thumbnail & publishing {}", picture);
            // must acknowledge that message has been already processed
            channel.basicAck(deliveryTag, false);


        } catch (IOException e) {
            LOG.warn("Error processing the message: {}", new String(message.getBody() + ":" + e.getMessage()));
            dlxProcessingErrorHandler.handleErrorProcessingMessage(message, channel, deliveryTag);
        }
    }

}
