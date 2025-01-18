package com.np.rabbitmq.consumer.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.np.rabbitmq.consumer.dto.Employee;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RetryMarketingConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(RetryMarketingConsumer.class);

    private final ObjectMapper objectMapper;

    public RetryMarketingConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

//    @RabbitListener(queues = "q.guideline2.marketing.work")
    public void consume(Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        var employee = objectMapper.readValue(message.getBody(), Employee.class);
        LOG.info("[Marketing] Consumed employee: {}", employee);
        channel.basicAck(deliveryTag, false);
    }
}
