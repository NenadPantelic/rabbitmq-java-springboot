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

@Component
public class RetryAccountingConsumer {

    private static final String DEAD_EXCHANGE_NAME = "x.guideline2.dead";
    private static final Logger LOG = LoggerFactory.getLogger(RetryAccountingConsumer.class);

    private final ObjectMapper objectMapper;
    private final DlxProcessingErrorHandler dlxProcessingErrorHandler;

    public RetryAccountingConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.dlxProcessingErrorHandler = new DlxProcessingErrorHandler(DEAD_EXCHANGE_NAME);
    }

//    @RabbitListener(queues = "q.guideline2.accounting.work")
    public void listen(Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        try {

            var employee = objectMapper.readValue(message.getBody(), Employee.class);
            if (employee.name() == null || employee.name().isEmpty()) {
                throw new IllegalArgumentException("Name is empty");
            }

            LOG.info("[Accounting] Consumed employee: {}", employee);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            LOG.warn("Error processing the message: {} : {}", new String(message.getBody()), e.getMessage());
            dlxProcessingErrorHandler.handleErrorProcessingMessage(message, channel, deliveryTag);
        }

    }
}
