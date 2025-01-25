package com.np.rabbitmq.two.consumer;

import com.np.rabbitmq.two.dto.DummyMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class DummyConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(DummyConsumer.class);

    //@RabbitListener(queues = "q.dummy")
    public void listenDummy(DummyMessage dummyMessage) {
        LOG.info("Dummy message received: {}", dummyMessage);
    }
}
