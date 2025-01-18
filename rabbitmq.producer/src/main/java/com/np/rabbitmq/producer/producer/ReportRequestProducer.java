package com.np.rabbitmq.producer.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.np.rabbitmq.producer.dto.ReportRequest;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class ReportRequestProducer {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public ReportRequestProducer(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendMessage(ReportRequest reportRequest) throws JsonProcessingException {
        var messageProperties = new MessageProperties();
        var delayInMillis = reportRequest.large() ? 2 * 60 * 1000L : 0L;

        messageProperties.setHeader("x-delay", Long.toString(delayInMillis));
        var json = objectMapper.writeValueAsString(reportRequest);

        var message = new Message(json.getBytes(StandardCharsets.UTF_8), messageProperties);
        rabbitTemplate.send("x.delayed", "delayThis", message);
    }
}
