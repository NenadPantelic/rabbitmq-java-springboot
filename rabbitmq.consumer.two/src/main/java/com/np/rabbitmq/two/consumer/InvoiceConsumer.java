package com.np.rabbitmq.two.consumer;

import com.np.rabbitmq.two.dto.InvoiceCreatedMessage;
import com.np.rabbitmq.two.dto.InvoicePaidMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
//@RabbitListener(queues = "q.invoice")
public class InvoiceConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(InvoiceConsumer.class);

    @RabbitHandler // based on payload processes the message (different message types)
    public void handleInvoiceCreated(InvoiceCreatedMessage message) {
        LOG.info("Invoice created: {}", message);
    }

    @RabbitHandler
    public void handleInvoicePaid(InvoicePaidMessage message) {
        LOG.info("Invoice paid: {}", message);
    }

    @RabbitHandler(isDefault = true) // there is no a dedicated handler for a particular message type, the default one
    // takes care of it
    public void handleDefault(Object message) {
        LOG.info("Default handler: {}", message);
    }
}
