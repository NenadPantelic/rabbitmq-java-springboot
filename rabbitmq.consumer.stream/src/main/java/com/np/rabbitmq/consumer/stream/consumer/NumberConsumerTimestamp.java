package com.np.rabbitmq.consumer.stream.consumer;

import com.np.rabbitmq.consumer.stream.config.RabbitmqStreamConfig;
import com.rabbitmq.stream.Message;
import com.rabbitmq.stream.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NumberConsumerTimestamp {

    private static final Logger LOG = LoggerFactory.getLogger(NumberConsumerTimestamp.class);

//    @RabbitListener(queues = RabbitmqStreamConfig.STREAM_NUMBER, containerFactory = "timestampContainerFactoryOne")
    public void absoluteOne(Message message, MessageHandler.Context context) {
        LOG.info("Timestamp 1 = {}, offset = {}", message.getBody(), context.offset());
    }
}
