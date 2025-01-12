package com.np.rabbitmq.consumer.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.np.rabbitmq.consumer.dto.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class AccountingConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(AccountingConsumer.class);

    private final ObjectMapper objectMapper;

    public AccountingConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "q.hr.accounting")
    public void consume(String message) throws JsonProcessingException {
        var employee = objectMapper.readValue(message, Employee.class);
        LOG.info("[Accounting] Consumed employee: {}", employee);
    }
}
