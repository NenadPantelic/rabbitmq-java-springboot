package com.np.rabbitmq.two.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class StreamHelloConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(StreamHelloConsumer.class);


    @RabbitListener(queues = "s.hello")
    public void listenHelloStream(String message) {
        LOG.info("Consumed from stream: {}", message);
    }

    @RabbitListener(queues = "q.hello")
    public void listenHelloQueue(String message) {
        LOG.info("Consumed from queue: {}", message);
    }
}
