package com.np.rabbitmq.consumer.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.np.rabbitmq.consumer.dto.Picture;
import com.np.rabbitmq.consumer.dto.ReportRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ReportRequestConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(ReportRequestConsumer.class);

    private final ObjectMapper objectMapper;

    public ReportRequestConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "q.delayed")
    public void listen(String message) throws JsonMappingException, JsonProcessingException {
        var reportRequest = objectMapper.readValue(message, ReportRequest.class);
        LOG.info("Consumed {}...", reportRequest);
    }
}
