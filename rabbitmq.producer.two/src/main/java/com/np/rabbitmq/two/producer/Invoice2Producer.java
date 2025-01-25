package com.np.rabbitmq.two.producer;

import com.np.rabbitmq.two.dto.InvoiceCancelledMessage;
import com.np.rabbitmq.two.dto.InvoiceCreatedMessage;
import com.np.rabbitmq.two.dto.InvoicePaidMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class Invoice2Producer {

    private static final Logger LOG = LoggerFactory.getLogger(Invoice2Producer.class);
    private static final String EXCHANGE = "x.invoice2";

    private final RabbitTemplate rabbitTemplate;

    public Invoice2Producer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendInvoiceCreated(InvoiceCreatedMessage message) {
        LOG.info("Sending {}", message);
//        rabbitTemplate.convertAndSend(EXCHANGE, "", message); // fanout exchange, no need for routing key
        rabbitTemplate.convertAndSend(EXCHANGE, message.invoiceNumber(), message); // consistent hash exchange, invoice number
        // is routing key
    }

    public void sendInvoicePaid(InvoicePaidMessage message) {
        LOG.info("Sending {}", message);
        rabbitTemplate.convertAndSend(EXCHANGE, message.invoiceNumber(), message); // consistent hash exchange, invoice number
        // is routing key
    }

    public void sendInvoiceCancelled(InvoiceCancelledMessage message) {
        LOG.info("Sending {}", message);
        rabbitTemplate.convertAndSend(EXCHANGE, message.invoiceNumber(), message); // consistent hash exchange, invoice number
        // is routing key
    }
}
