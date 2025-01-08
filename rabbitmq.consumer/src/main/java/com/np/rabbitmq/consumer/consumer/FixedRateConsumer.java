package com.np.rabbitmq.consumer.consumer;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class FixedRateConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(FixedRateConsumer.class);

    @RabbitListener(queues = "np.fixedrate", concurrency = "3-7") // min 3 threads, max 7 threads
    public void consume(String message) throws InterruptedException {
        LOG.info("Thread {} is consuming {}...", Thread.currentThread().getName(), message);
        TimeUnit.MILLISECONDS.sleep(2);
    }

}
