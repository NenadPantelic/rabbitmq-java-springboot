package com.np.rabbitmq.two.consumer;

import com.np.rabbitmq.two.dto.DummyMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class DummyPrefetchConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(DummyPrefetchConsumer.class);

    //@RabbitListener(queues = "q.dummy", concurrency = "2")
    public void listenDummy(DummyMessage dummyMessage) throws InterruptedException {
        LOG.info("Message consumed: {}", dummyMessage);
        TimeUnit.SECONDS.sleep(20);
    }
}
