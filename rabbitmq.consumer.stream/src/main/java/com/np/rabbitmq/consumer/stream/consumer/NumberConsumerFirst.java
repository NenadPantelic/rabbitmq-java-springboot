package com.np.rabbitmq.consumer.stream.consumer;

import com.np.rabbitmq.consumer.stream.config.RabbitmqStreamConfig;
import com.rabbitmq.stream.Message;
import com.rabbitmq.stream.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NumberConsumerFirst {

    private static final Logger LOG = LoggerFactory.getLogger(NumberConsumerFirst.class);

    //    @RabbitListener(queues = RabbitmqStreamConfig.STREAM_NUMBER, containerFactory = "firstContainerFactoryOne")
    public void firstOne(Message message, MessageHandler.Context context) {
        LOG.info("First 1 = {}, offset = {}", message.getBody(), context.offset());
    }

//    @RabbitListener(queues = RabbitmqStreamConfig.STREAM_NUMBER, containerFactory = "firstContainerFactoryTwo")
    public void firstTwo(Message message, MessageHandler.Context context) {
        LOG.info("First 1 = {}, offset = {}", message.getBody(), context.offset());
    }

//    @RabbitListener(queues = RabbitmqStreamConfig.STREAM_NUMBER, containerFactory = "firstContainerFactoryThree")
    public void firstThree(Message message, MessageHandler.Context context) {
        LOG.info("First 1 = {}, offset = {}", message.getBody(), context.offset());
        // manually updating the offset; without this message will be processed if the consumer is restarted
        context.storeOffset();
    }
}
