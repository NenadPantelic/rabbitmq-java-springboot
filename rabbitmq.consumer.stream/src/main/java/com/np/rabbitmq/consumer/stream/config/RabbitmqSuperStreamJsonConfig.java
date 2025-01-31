package com.np.rabbitmq.consumer.stream.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.np.rabbitmq.consumer.stream.dto.Invoice;
import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.OffsetSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.rabbit.stream.config.StreamRabbitListenerContainerFactory;
import org.springframework.rabbit.stream.listener.StreamListenerContainer;

import java.util.concurrent.TimeUnit;

@Configuration
public class RabbitmqSuperStreamJsonConfig {

    private static final Logger LOG = LoggerFactory.getLogger(RabbitmqSuperStreamConfig.class);

    public static final String STREAM_INVOICE_NAME = "s.invoice";

    @Autowired
    private ObjectMapper objectMapper;

    @Bean(name = "superStreamInvoiceContainer")
    StreamListenerContainer superStreamInvoiceContainer() {
        var environment = Environment.builder()
                .username("admin")
                .password("admin")
                .maxConsumersByConnection(1)
                .build();
        var container = new StreamListenerContainer(environment);

        container.setConsumerCustomizer((id, builder) -> builder.offset(OffsetSpecification.first()));
        container.superStream("s.super.invoice", "my-super-stream-invoice-consumer");
        container.setupMessageListener(message -> {
            try {
                var invoice = objectMapper.readValue(message.getBody(), Invoice.class);
                LOG.info("Invoice: {}", invoice);
                TimeUnit.SECONDS.sleep(4);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return container;
    }
}
