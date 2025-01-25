package com.np.rabbitmq.two.consumer;

import com.np.rabbitmq.two.dto.DummyMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class MultiplePrefetchConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(MultiplePrefetchConsumer.class);

    //@RabbitListener(queues = "q.transaction", concurrency = "2", containerFactory = "prefetchTwoContainerFactory")
    public void listenTransaction(DummyMessage message) throws InterruptedException {
        LOG.info("Consuming transaction: {}", message.content());
        TimeUnit.SECONDS.sleep(1);
    }

    //@RabbitListener(queues = "q.scheduler", concurrency = "2",containerFactory = "prefetchOneContainerFactory")
    public void listenScheduler(DummyMessage message) throws InterruptedException {
        LOG.info("Consuming scheduler: {}", message.content());
        TimeUnit.SECONDS.sleep(60);
    }
}
