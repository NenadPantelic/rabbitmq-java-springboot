package com.np.rabbitmq.consumer.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class HelloRabbitConsumer {

    private final RabbitTemplate rabbitTemplate;

    public HelloRabbitConsumer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

//    @RabbitListener(queues = "np.hello")
    public void listen(String message) {
        System.out.println("Consuming: " + message);
    }
}
