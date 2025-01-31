package com.np.rabbitmq.consumer.stream.config;

import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.OffsetSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.rabbit.stream.listener.StreamListenerContainer;

import java.util.concurrent.TimeUnit;

@Configuration
public class RabbitmqSuperStreamConfig {

    private static final Logger LOG = LoggerFactory.getLogger(RabbitmqSuperStreamConfig.class);

//    @Bean
    StreamListenerContainer superStreamNumberContainer() {
        var environment = Environment.builder()
                .username("admin")
                .password("admin")
                .maxConsumersByConnection(1)
                .build();
        var container = new StreamListenerContainer(environment);

        container.setConsumerCustomizer((id, builder) -> builder.offset(OffsetSpecification.first()));
        container.superStream("s.super.number", "my-super-stream-number-consumer");
        container.setupMessageListener(message -> {
            LOG.info("Message listened: {}", new String(message.getBody()));
            try {
                TimeUnit.SECONDS.sleep(4);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        return container;
    }
}
