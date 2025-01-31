package com.np.rabbitmq.producer.stream.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.np.rabbitmq.producer.stream.dto.Invoice;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.rabbit.stream.producer.RabbitStreamTemplate;
import org.springframework.stereotype.Component;

@Component
public class SuperStreamInvoiceProducer {

    private final RabbitStreamTemplate rabbitStreamTemplate;
    private final ObjectMapper objectMapper;

    public SuperStreamInvoiceProducer(@Qualifier("superStreamInvoiceTemplate") RabbitStreamTemplate rabbitStreamTemplate,
                                      ObjectMapper objectMapper) {
        this.rabbitStreamTemplate = rabbitStreamTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendInvoiceUsingRabbitStreamTemplate(Invoice invoice) throws JsonProcessingException {
        var jsonBody = objectMapper.writeValueAsBytes(invoice);
        var message = rabbitStreamTemplate.messageBuilder()
                .addData(jsonBody)
                .properties()
                .messageId(invoice.invoiceNumber())
                .messageBuilder()
                .build();
        rabbitStreamTemplate.send(message);
    }
}
