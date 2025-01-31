package com.np.rabbitmq.producer.stream;

import com.np.rabbitmq.producer.stream.dto.Invoice;
import com.np.rabbitmq.producer.stream.dto.InvoiceStatus;
import com.np.rabbitmq.producer.stream.producer.StreamInvoiceProducer;
import com.np.rabbitmq.producer.stream.producer.StreamNumberProducer;
import com.np.rabbitmq.producer.stream.producer.SuperStreamInvoiceProducer;
import com.np.rabbitmq.producer.stream.producer.SuperStreamNumberProducer;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class Application implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Autowired
    private StreamNumberProducer streamNumberProducer;

    @Autowired
    private SuperStreamNumberProducer superStreamNumberProducer;

    @Autowired
    private StreamInvoiceProducer streamInvoiceProducer;

    @Autowired
    private SuperStreamInvoiceProducer superStreamInvoiceProducer;

    // AMQP connection must be open in order to declare beans
    // that's why we introduced this variable
    // see [***]Test[***]
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void run(String... args) throws Exception {
        // [***]Test[***]
        rabbitTemplate.convertAndSend("Test");
        // stream producer
//        streamNumberProducer.sendNumbers(0, 5);

        // super stream producer
//        superStreamNumberProducer.sendNumbersUsingRabbitTemplate(0, 5);
//        superStreamNumberProducer.sendNumbersUsingRabbitStreamTemplate(0, 5);

        // stream JSON producer
//        for (int i = 0; i < 10; i++) {
//            var invoice = new Invoice(
//                    "INV-" + i,
//                    InvoiceStatus.CREATED,
//                    ThreadLocalRandom.current().nextInt(100, 1000)
//            );
//
//            if (i % 2 == 0) {
//                streamInvoiceProducer.sendInvoiceUsingRabbitTemplate(invoice);
//            } else {
//                streamInvoiceProducer.sendInvoiceUsingRabbitStreamTemplate(invoice);
//            }
//
//            TimeUnit.MILLISECONDS.sleep(100);
//        }
//
//        System.out.println("All invoices have been sent...");

        // super stream JSON producer
        for (int i = 0; i < 10; i++) {
            var invoiceAmount = ThreadLocalRandom.current().nextInt(100, 1000);
            var invoiceCreated = new Invoice(
                    "INV-" + i,
                    InvoiceStatus.CREATED,
                    invoiceAmount
            );

            var invoiceApproved = new Invoice(
                    "INV-" + i,
                    InvoiceStatus.APPROVED,
                    invoiceAmount
            );

            superStreamInvoiceProducer.sendInvoiceUsingRabbitStreamTemplate(invoiceCreated);
            superStreamInvoiceProducer.sendInvoiceUsingRabbitStreamTemplate(invoiceApproved);
            TimeUnit.MILLISECONDS.sleep(100);
        }

        System.out.println("All invoices have been sent...");
    }
}
