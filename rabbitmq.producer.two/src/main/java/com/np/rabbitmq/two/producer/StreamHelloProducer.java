package com.np.rabbitmq.two.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.rabbit.stream.producer.RabbitStreamTemplate;
import org.springframework.stereotype.Component;

@Component
public class StreamHelloProducer {

    private static final Logger LOG = LoggerFactory.getLogger(StreamHelloProducer.class);

    @Autowired
    @Qualifier("rabbitStreamTemplateHello")
    private RabbitStreamTemplate rabbitStreamTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendHello(String s) {
        LOG.info("Sending {}", s);
        rabbitStreamTemplate.convertAndSend(s);
    }

    public void sendHelloUsingExchange(String s) {
        LOG.info("Sending {}", s);
        rabbitTemplate.convertAndSend("x.hello", "rk.hello", s);
    }
}
