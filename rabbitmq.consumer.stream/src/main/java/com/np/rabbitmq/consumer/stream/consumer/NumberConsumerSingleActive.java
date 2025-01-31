package com.np.rabbitmq.consumer.stream.consumer;

import com.np.rabbitmq.consumer.stream.config.RabbitmqStreamConfig;
import com.rabbitmq.stream.Message;
import com.rabbitmq.stream.MessageHandler;
import org.apache.qpid.proton.amqp.messaging.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class NumberConsumerSingleActive {

    private static final Logger LOG = LoggerFactory.getLogger(NumberConsumerAbsolute.class);

//    @RabbitListener(queues = RabbitmqStreamConfig.STREAM_NUMBER, containerFactory = "singleActiveContainerFactoryOne")
    public void singleActiveOne(Message message, MessageHandler.Context context) throws InterruptedException {
        var data = (Data) message.getBody();
        LOG.info("Single active consumer: body = {}, offset = {}", data.getValue(), context.offset());
        TimeUnit.MILLISECONDS.sleep(500);
    }
}
