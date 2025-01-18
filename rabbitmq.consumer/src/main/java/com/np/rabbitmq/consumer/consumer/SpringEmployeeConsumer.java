package com.np.rabbitmq.consumer.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.np.rabbitmq.consumer.dto.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SpringEmployeeConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(SpringEmployeeConsumer.class);

    private final ObjectMapper objectMapper;

    public SpringEmployeeConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "q.spring2.accounting.work")
    public void listenAccounting(String message) throws IOException {
        var employee = objectMapper.readValue(message, Employee.class);
        LOG.info("Consumed employee: {}", employee);

        if (employee.name() == null || employee.name().isBlank()) {
            throw new IOException("Employee name is empty");
        }

        LOG.info("Process accounting: {}", employee);
    }

    @RabbitListener(queues = "q.spring2.marketing.work")
    public void listenMarketing(String message) throws IOException {
        var employee = objectMapper.readValue(message, Employee.class);
        LOG.info("Consumed employee: {}", employee);
        LOG.info("Process marketing: {}", employee);
    }
}
