package com.np.rabbitmq.two.consumer;

import com.np.rabbitmq.two.dto.DummyMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class AnotherDummyConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(AnotherDummyConsumer.class);

    //    @RabbitListener(queues = "q.another-dummy") // if the queue does not exist, will throw exceptions
    @RabbitListener(bindings =
    @QueueBinding(value =
    @Queue(name = "q.auto-dummy", durable = "true"),
            exchange = @Exchange(name = "x.auto-dummy", type = ExchangeTypes.DIRECT, durable = "true"),
            key = "routing-key",
            ignoreDeclarationExceptions = "true"
    ))
    public void listenDummy(DummyMessage dummyMessage) {
        LOG.info("Dummy message received: {}", dummyMessage);
    }
}
