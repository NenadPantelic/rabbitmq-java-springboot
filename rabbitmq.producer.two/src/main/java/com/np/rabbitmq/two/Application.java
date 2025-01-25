package com.np.rabbitmq.two;

import com.np.rabbitmq.two.dto.DummyMessage;
import com.np.rabbitmq.two.dto.InvoiceCancelledMessage;
import com.np.rabbitmq.two.dto.InvoiceCreatedMessage;
import com.np.rabbitmq.two.dto.InvoicePaidMessage;
import com.np.rabbitmq.two.producer.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class Application implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Autowired
    private DummyProducer dummyProducer;

    @Autowired
    private MultiplePrefetchProducer multiplePrefetchProducer;

    @Autowired
    private InvoiceProducer invoiceProducer;

    @Autowired
    private SingleActiveProducer singleActiveProducer;

    @Autowired
    private ReliableProducer reliableProducer;

    @Autowired
    private Invoice2Producer invoice2Producer;

    @Autowired
    private AnotherDummyProducer anotherDummyProducer;

    @Override
    public void run(String... args) throws Exception {
//        var dummyMessage = new DummyMessage("Content", 1);
//        dummyProducer.sendMessage(dummyMessage);
        // scheduled consumer
//        for (int i = 0; i < 10_000; i++) {
//            var dummyMessage = new DummyMessage("Content " + i, i);
//            dummyProducer.sendMessage(dummyMessage);
//            TimeUnit.SECONDS.sleep(1);
//        }

        // consumer prefetch
//        for (int i = 0; i < 500; i++) {
//            var dummyMessage = new DummyMessage("Content " + i, i);
//            dummyProducer.sendMessage(dummyMessage);
//            TimeUnit.SECONDS.sleep(1);
//        }

        // multiple prefetch values
//        multiplePrefetchProducer.simulateTransaction();
//        multiplePrefetchProducer.simulateScheduler();
//        System.out.println("All data sent");

        // one queue + multiple message types
//        var randomInvoiceNumber = "INV-" + ThreadLocalRandom.current().nextInt(100, 200);
//        var invoiceCreatedMessage = new InvoiceCreatedMessage(
//                155.75, LocalDate.now(), "USD", randomInvoiceNumber
//        );
//        invoiceProducer.sendInvoiceCreated(invoiceCreatedMessage);
//
//        randomInvoiceNumber = "INV-" + ThreadLocalRandom.current().nextInt(200, 300);
//        var randomPaymentNumber = "PAY-" + ThreadLocalRandom.current().nextInt(1000, 2000);
//        var invoicePaidMessage = new InvoicePaidMessage(
//                randomInvoiceNumber, LocalDate.now(), randomPaymentNumber
//        );
//        invoiceProducer.sendInvoicePaid(invoicePaidMessage);
//
//        randomInvoiceNumber = "INV-" + ThreadLocalRandom.current().nextInt(300, 400);
//        var invoiceCancelledMessage = new InvoiceCancelledMessage(
//                LocalDate.now(), randomInvoiceNumber, "Bad account details."
//        );
//        invoiceProducer.sendInvoiceCancelled(invoiceCancelledMessage);

        // consistent hashing
//        for (int i = 0; i < 200; i++) {
//            var invoiceNumber = "INV-" + (i % 60);
//            var invoice = new InvoiceCreatedMessage(
//                    ThreadLocalRandom.current().nextDouble(), LocalDate.now(), "USD", invoiceNumber
//            );
//            invoiceProducer.sendInvoiceCreated(invoice);
//        }

        // single active consumer example
//        singleActiveProducer.sendDummy();
//        var dummyMessage = new DummyMessage("Content", 1);
//        reliableProducer.sendDummyMessageWithInvalidRoutingKey(dummyMessage);
//        reliableProducer.sendDummyMessageToNonExistentExchange(dummyMessage);

        // request-reply pattern
//        for (int i = 0; i < 10; i++) {
//            var invoiceNumber = "INV-" + i;
//            var invoiceCancelledMessage = new InvoiceCancelledMessage(
//                    LocalDate.now(),
//                    invoiceNumber,
//                    "Invoice cancelled " + i
//            );
//
//            invoice2Producer.sendInvoiceCancelled(invoiceCancelledMessage);
//        }

        var dummyMessage = new DummyMessage("Content", 1);
//        anotherDummyProducer.sendMessage(dummyMessage);
    }
}
