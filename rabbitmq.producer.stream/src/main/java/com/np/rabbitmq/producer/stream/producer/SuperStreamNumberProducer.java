package com.np.rabbitmq.producer.stream.producer;

import com.np.rabbitmq.producer.stream.config.RabbitmqSuperStreamConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.rabbit.stream.producer.RabbitStreamTemplate;
import org.springframework.stereotype.Component;

@Component
public class SuperStreamNumberProducer {

    private static final Logger LOG = LoggerFactory.getLogger(SuperStreamNumberProducer.class);

    private final RabbitTemplate rabbitTemplate;
    private final RabbitStreamTemplate rabbitStreamTemplate;

    public SuperStreamNumberProducer(RabbitTemplate rabbitTemplate,
                                     @Qualifier("superStreamNumberTemplate") RabbitStreamTemplate rabbitStreamTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitStreamTemplate = rabbitStreamTemplate;
    }

    public void sendNumbersUsingRabbitTemplate(int start, int end) {
        for (int i = start; i < end; i++) {
            var payload = "Number " + i;
            var routingKey = Integer.toString(i % RabbitmqSuperStreamConfig.SUPER_STREAM_NUMBER_PARTITIONS);
            rabbitTemplate.convertAndSend(RabbitmqSuperStreamConfig.SUPER_STREAM_NUMBER_NAME, routingKey, payload);
        }

        LOG.info("Sent data to super stream: {} to {}", start, (end - 1));
    }

    public void sendNumbersUsingRabbitStreamTemplate(int start, int end) {
        for (int i = start; i < end; i++) {
            var payload = "Number " + i;
            var message = rabbitStreamTemplate.messageBuilder()
                    .addData(payload.getBytes())
                    .properties()
                    .messageId(i)
                    .messageBuilder()
                    .build();

            rabbitStreamTemplate.send(message);
        }

        LOG.info("Sent data to super stream: {} to {}", start, (end - 1));
    }
}
