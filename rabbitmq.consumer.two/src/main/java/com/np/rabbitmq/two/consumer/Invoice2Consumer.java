package com.np.rabbitmq.two.consumer;

import com.np.rabbitmq.two.dto.InvoiceCancelledMessage;
import com.np.rabbitmq.two.dto.InvoiceCreatedMessage;
import com.np.rabbitmq.two.dto.InvoicePaidMessage;
import com.np.rabbitmq.two.dto.PaymentCancelStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

@Component
//@RabbitListener(queues = "q.invoice2")
public class Invoice2Consumer {

    private static final Logger LOG = LoggerFactory.getLogger(Invoice2Consumer.class);

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

    @RabbitHandler
//    @SendTo("x.invoice.cancel/routingKey")
    @SendTo("x.invoice2.cancel/") // fanout exchange; where to send reply
    public PaymentCancelStatus handleInvoiceCancelled(InvoiceCancelledMessage message) {
        LOG.info("Consume: {}", message);
        var randomStatus = ThreadLocalRandom.current().nextBoolean();
        // reply message
        return new PaymentCancelStatus(randomStatus, LocalDate.now(), message.invoiceNumber());
    }
}
