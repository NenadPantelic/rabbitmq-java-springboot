package com.np.rabbitmq.two.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitmqSchemaConfig {

    @Bean
    FanoutExchange fanoutExchange() {
        return new FanoutExchange("x.another-dummy", true, false, null);
    }

    DirectExchange directExchange() {
        return new DirectExchange("x.another-dummy-direct-exchange", true, false, null);
    }

    TopicExchange topicExchange() {
        return new TopicExchange("x.another-dummy-topic-exchange", true, false, null);
    }

    HeadersExchange headersExchange() {
        return new HeadersExchange("x.another-dummy-headers-exchange", true, false, null);
    }

    /// queue ///
    @Bean
    Queue queue() {
        return new Queue("q.another-dummy", true, false, false);
    }

    /// binding ///
    @Bean
    Binding binding() {
        return new Binding(
                "q.another-dummy",
                Binding.DestinationType.QUEUE,
                "x-another-dummy",
                "",
                null
        );

        // another way
//        return BindingBuilder.bind(queue())
//                .to(fanoutExchange()) // fanout
//                .to(new DirectExchange("x.direct")).with("routingKey"); // direct
    }

    //// another way ////
    // put everything in declarable
    @Bean
    // safe way, idempotent
    Declarables createRabbitmqSchema() {
        return new Declarables(
                new FanoutExchange("x.another-dummy-2", true, false, null),
                new Queue("q.another-dummy-2", true, false, false),
                new Binding(
                        "q.another-dummy-2",
                        Binding.DestinationType.QUEUE,
                        "x-another-dummy-2",
                        "",
                        null
                )
        );
    }
}
