package com.np.rabbitmq.two.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitmqConfig {

    @Bean
    ObjectMapper objectMapper() {
        return JsonMapper.builder().findAndAddModules().build();
    }

    @Bean
    Jackson2JsonMessageConverter jackson2JsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper); // Rabbit template will automatically convert Java object
        // to JSON body as string
    }

    @Bean
    RabbitListenerContainerFactory<SimpleMessageListenerContainer> prefetchOneContainerFactory(SimpleRabbitListenerContainerFactoryConfigurer configurer,
                                                                                               ConnectionFactory connectionFactory) {
        var factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setPrefetchCount(1);
        return factory;
    }

    @Bean
    RabbitListenerContainerFactory<SimpleMessageListenerContainer> prefetchTwoContainerFactory(SimpleRabbitListenerContainerFactoryConfigurer configurer,
                                                                                               ConnectionFactory connectionFactory) {
        var factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setPrefetchCount(250);
        return factory;
    }
}
