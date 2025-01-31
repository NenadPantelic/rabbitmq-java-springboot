package com.np.rabbitmq.consumer.stream.config;

import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.OffsetSpecification;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.rabbit.stream.config.StreamRabbitListenerContainerFactory;
import org.springframework.rabbit.stream.listener.StreamListenerContainer;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Configuration
public class RabbitmqStreamConfig {

    public static final String STREAM_NUMBER = "s.number";

    ////// when consumer starts first //////
    private static final String CONSUMER_OFFSET_ABSOLUTE_01 = "consumer-offset-absolute-01";
    private static final String CONSUMER_OFFSET_FIRST_01 = "consumer-offset-first-01";
    private static final String CONSUMER_OFFSET_LAST_01 = "consumer-offset-last-01";
    private static final String CONSUMER_OFFSET_NEXT_01 = "consumer-offset-next-01";
    private static final String CONSUMER_OFFSET_TIMESTAMP_01 = "consumer-offset-timestamp-01";

    ////// when producer starts first //////
    private static final String CONSUMER_OFFSET_ABSOLUTE_02 = "consumer-offset-absolute-02";
    private static final String CONSUMER_OFFSET_FIRST_02 = "consumer-offset-first-02";
    private static final String CONSUMER_OFFSET_LAST_02 = "consumer-offset-last-02";
    private static final String CONSUMER_OFFSET_NEXT_02 = "consumer-offset-next-02";
    private static final String CONSUMER_OFFSET_TIMESTAMP_02 = "consumer-offset-timestamp-02";


    ////// for manual offset tracking //////
    private static final String CONSUMER_OFFSET_FIRST_03 = "consumer-offset-first-03";

    ////// single active consumer //////
    private static final String CONSUMER_SINGLE_ACTIVE_01 = "consumer-single-active-01";


    ////// when consumer starts first //////
    @Bean
    RabbitListenerContainerFactory<StreamListenerContainer> absoluteContainerFactoryOne(Environment environment) {
        var factory = new StreamRabbitListenerContainerFactory(environment);
        factory.setNativeListener(true);
        factory.setConsumerCustomizer((id, builder) -> {
            // the default setting is autoTrackingStrategy, no need to be explicitly set
            builder.name(CONSUMER_OFFSET_ABSOLUTE_01).offset(OffsetSpecification.offset(3)).autoTrackingStrategy();
        });

        return factory;
    }

    @Bean
    RabbitListenerContainerFactory<StreamListenerContainer> firstContainerFactoryOne(Environment environment) {
        var factory = new StreamRabbitListenerContainerFactory(environment);
        factory.setNativeListener(true);
        factory.setConsumerCustomizer((id, builder) -> {
            builder.name(CONSUMER_OFFSET_FIRST_01).offset(OffsetSpecification.first()).autoTrackingStrategy();
        });

        return factory;
    }

    @Bean
    RabbitListenerContainerFactory<StreamListenerContainer> lastContainerFactoryOne(Environment environment) {
        var factory = new StreamRabbitListenerContainerFactory(environment);
        factory.setNativeListener(true);
        factory.setConsumerCustomizer((id, builder) -> {
            builder.name(CONSUMER_OFFSET_LAST_01).offset(OffsetSpecification.last()).autoTrackingStrategy();
        });

        return factory;
    }

    @Bean
    RabbitListenerContainerFactory<StreamListenerContainer> nextContainerFactoryOne(Environment environment) {
        var factory = new StreamRabbitListenerContainerFactory(environment);
        factory.setNativeListener(true);
        factory.setConsumerCustomizer((id, builder) -> {
            builder.name(CONSUMER_OFFSET_NEXT_01).offset(OffsetSpecification.next()).autoTrackingStrategy();
        });

        return factory;
    }

    @Bean
    RabbitListenerContainerFactory<StreamListenerContainer> timestampContainerFactoryOne(Environment environment) {
        var factory = new StreamRabbitListenerContainerFactory(environment);
        var offsetTimestamp = ZonedDateTime.now(ZoneOffset.UTC).minusMinutes(5).toEpochSecond() * 1000; // milliseconds
        factory.setNativeListener(true);
        factory.setConsumerCustomizer((id, builder) -> {
            builder.name(CONSUMER_OFFSET_TIMESTAMP_01).offset(OffsetSpecification.timestamp(offsetTimestamp)).autoTrackingStrategy();
        });

        return factory;
    }


    ////// when producer starts first //////
    @Bean
    RabbitListenerContainerFactory<StreamListenerContainer> absoluteContainerFactoryTwo(Environment environment) {
        var factory = new StreamRabbitListenerContainerFactory(environment);
        factory.setNativeListener(true);
        factory.setConsumerCustomizer((id, builder) -> {
            // the default setting is autoTrackingStrategy, no need to be explicitly set
            builder.name(CONSUMER_OFFSET_ABSOLUTE_02).offset(OffsetSpecification.offset(3)).autoTrackingStrategy();
        });

        return factory;
    }

    @Bean
    RabbitListenerContainerFactory<StreamListenerContainer> firstContainerFactoryTwo(Environment environment) {
        var factory = new StreamRabbitListenerContainerFactory(environment);
        factory.setNativeListener(true);
        factory.setConsumerCustomizer((id, builder) -> {
            builder.name(CONSUMER_OFFSET_FIRST_02).offset(OffsetSpecification.first()).autoTrackingStrategy();
        });

        return factory;
    }

    @Bean
    RabbitListenerContainerFactory<StreamListenerContainer> lastContainerFactoryTwo(Environment environment) {
        var factory = new StreamRabbitListenerContainerFactory(environment);
        factory.setNativeListener(true);
        factory.setConsumerCustomizer((id, builder) -> {
            builder.name(CONSUMER_OFFSET_LAST_02).offset(OffsetSpecification.last()).autoTrackingStrategy();
        });

        return factory;
    }

    @Bean
    RabbitListenerContainerFactory<StreamListenerContainer> nextContainerFactoryTwo(Environment environment) {
        var factory = new StreamRabbitListenerContainerFactory(environment);
        factory.setNativeListener(true);
        factory.setConsumerCustomizer((id, builder) -> {
            builder.name(CONSUMER_OFFSET_NEXT_02).offset(OffsetSpecification.next()).autoTrackingStrategy();
        });

        return factory;
    }

    @Bean
    RabbitListenerContainerFactory<StreamListenerContainer> timestampContainerFactoryTwo(Environment environment) {
        var factory = new StreamRabbitListenerContainerFactory(environment);
        var offsetTimestamp = ZonedDateTime.now(ZoneOffset.UTC).minusMinutes(5).toEpochSecond() * 1000; // milliseconds
        factory.setNativeListener(true);
        factory.setConsumerCustomizer((id, builder) -> {
            builder.name(CONSUMER_OFFSET_TIMESTAMP_02).offset(OffsetSpecification.timestamp(offsetTimestamp)).autoTrackingStrategy();
        });

        return factory;
    }

    ////// manual offset tracking //////
    @Bean
    RabbitListenerContainerFactory<StreamListenerContainer> firstContainerFactoryThree(Environment environment) {
        var factory = new StreamRabbitListenerContainerFactory(environment);
        factory.setNativeListener(true);
        factory.setConsumerCustomizer((id, builder) -> {
            builder.name(CONSUMER_OFFSET_FIRST_01).offset(OffsetSpecification.first()).manualTrackingStrategy();
        });

        return factory;
    }

    ////// single active consumer //////
    @Bean
    RabbitListenerContainerFactory<StreamListenerContainer> singleActiveContainerFactoryOne(Environment environment) {
        var factory = new StreamRabbitListenerContainerFactory(environment);
        factory.setNativeListener(true);
        factory.setConsumerCustomizer((id, builder) -> builder.name(CONSUMER_OFFSET_ABSOLUTE_01)
                .offset(OffsetSpecification.next())
                .singleActiveConsumer()
                .autoTrackingStrategy());

        return factory;
    }
}
