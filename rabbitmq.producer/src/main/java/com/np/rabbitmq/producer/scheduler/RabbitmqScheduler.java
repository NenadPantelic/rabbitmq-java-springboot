package com.np.rabbitmq.producer.scheduler;

import com.np.rabbitmq.producer.client.RabbitmqClient;
import com.np.rabbitmq.producer.dto.RabbitmqQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RabbitmqScheduler {

    private static final Logger LOG = LoggerFactory.getLogger(RabbitmqScheduler.class);

    private final RabbitmqClient rabbitmqClient;

    public RabbitmqScheduler(RabbitmqClient rabbitmqClient) {
        this.rabbitmqClient = rabbitmqClient;
    }

    @Scheduled(fixedRate = 90000)
    void sweepDirtyQueues() {
        try {
            var dirtyQueues = rabbitmqClient.getAllQueues()
                    .stream()
                    .filter(RabbitmqQueue::isDirty)
                    .toList();
            dirtyQueues.forEach(queue -> LOG.info("Queue {} has {} unprocessed messages", queue.name(), queue.messages()));
        } catch (Exception e) {
            LOG.warn("Cannot sweep dirty queues due to {}", e.getMessage(), e);
        }
    }
}
