package com.np.rabbitmq.two.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class RabbitmqScheduler {

    private static final Logger LOG = LoggerFactory.getLogger(RabbitmqScheduler.class);

    private final RabbitListenerEndpointRegistry registry;

    public RabbitmqScheduler(RabbitListenerEndpointRegistry registry) {
        this.registry = registry;
    }

    // 19:44:00
    @Scheduled(cron = "0 44 19 * * *")
    public void stopAll() {
        registry.getListenerContainers().forEach(listener -> {
            LOG.info("Stopping container {}", listener);
            listener.stop();
        });
    }


    // 19:47:00
    // <second> <minute> <hour> <day-of-month> <month> <day-of-week>
    @Scheduled(cron = "0 47 19 * * *")
    public void startAll() {
        registry.getListenerContainers().forEach(listener -> {
            LOG.info("Starting container {}", listener);
            listener.start();
        });
    }
}
