package com.np.rabbitmq.consumer.stream.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.np.rabbitmq.consumer.stream.config.RabbitmqStreamConfig;
import com.np.rabbitmq.consumer.stream.config.RabbitmqStreamJsonConfig;
import com.np.rabbitmq.consumer.stream.dto.Invoice;
import com.rabbitmq.stream.Message;
import com.rabbitmq.stream.MessageHandler;
import org.apache.qpid.proton.amqp.messaging.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class InvoiceConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(InvoiceConsumer.class);

    private final ObjectMapper objectMapper;

    public InvoiceConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = RabbitmqStreamJsonConfig.STREAM_INVOICE_NAME)
    public void listenDefault(Invoice message) {
        LOG.info("listenDefault: {}", message);
    }

    @RabbitListener(queues = RabbitmqStreamJsonConfig.STREAM_INVOICE_NAME, containerFactory = "invoiceContainerFactoryOne")
    public void listenWithContainerFactoryOne(String message) throws JsonProcessingException {
        LOG.info("listenWithContainerFactoryOne JSON string: {}", message);
        var invoice = objectMapper.readValue(message, Invoice.class);
        LOG.info("listenWithContainerFactoryOne: {}", invoice);
    }

    @RabbitListener(queues = RabbitmqStreamJsonConfig.STREAM_INVOICE_NAME, containerFactory = "invoiceContainerFactoryTwo")
    public void listenWithContainerFactoryTwo(Message message, MessageHandler.Context context) throws IOException {
        var data = (Data) message.getBody();
        var invoice = objectMapper.readValue(data.getValue().getArray(), Invoice.class);
        LOG.info("listenWithContainerFactoryTwo: {}", invoice);
    }

}
