package com.np.rabbitmq.producer;

import com.np.rabbitmq.producer.producer.HelloRabbitProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.ThreadLocalRandom;

@EnableScheduling
@SpringBootApplication
public class Application implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Autowired
    private HelloRabbitProducer helloRabbitProducer;


    @Override
    public void run(String... args) throws Exception {
     //   helloRabbitProducer.sendHello("My name " + ThreadLocalRandom.current().nextInt());
    }
}
