package com.np.rabbitmq.producer.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class HelloRabbitProducer {

    private final RabbitTemplate rabbitTemplate;

    public HelloRabbitProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendHello(String name) {
        // Spring RabbitMQ publish messages as persistent (message restored on server restart - if the queue is also
        // durable)
        // queue name
        // message
        rabbitTemplate.convertAndSend("np.hello", "Hello " + name);
    }
}
