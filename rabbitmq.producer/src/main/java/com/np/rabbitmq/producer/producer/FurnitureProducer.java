package com.np.rabbitmq.producer.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.np.rabbitmq.producer.dto.Furniture;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class FurnitureProducer {

    private static final String COLOR_HEADER = "color";
    private static final String MATERIAL_HEADER = "material";
    private static final String PRICE_HEADER = "price";

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public FurnitureProducer(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendMessage(Furniture furniture) throws JsonProcessingException {
        var messageProperties = new MessageProperties();
        messageProperties.setHeader(COLOR_HEADER, furniture.color());
        messageProperties.setHeader(MATERIAL_HEADER, furniture.material());
        messageProperties.setHeader(PRICE_HEADER, furniture.price());

        var json = objectMapper.writeValueAsString(furniture);
        var message = new Message(json.getBytes(StandardCharsets.UTF_8), messageProperties);
        rabbitTemplate.send("x.promotion", "", message);
    }
}
