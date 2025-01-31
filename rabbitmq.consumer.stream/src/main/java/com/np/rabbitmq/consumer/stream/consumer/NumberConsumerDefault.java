package com.np.rabbitmq.consumer.stream.consumer;

import com.np.rabbitmq.consumer.stream.config.RabbitmqStreamConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NumberConsumerDefault {

    private static final Logger LOG = LoggerFactory.getLogger(NumberConsumerDefault.class);

//    @RabbitListener(queues = RabbitmqStreamConfig.STREAM_NUMBER)
    public void listen(String message) {
        LOG.info("Default listener: message = {}", message);
    }

//    @RabbitListener(queues = RabbitmqStreamConfig.STREAM_NUMBER)
    public void listen(Message message) {
        LOG.info("Default listener: message = {}", message);
    }
}
