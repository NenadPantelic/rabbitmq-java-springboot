package com.np.rabbitmq.producer;

import com.np.rabbitmq.producer.dto.Furniture;
import com.np.rabbitmq.producer.dto.Picture;
import com.np.rabbitmq.producer.dto.Employee;
import com.np.rabbitmq.producer.dto.ReportRequest;
import com.np.rabbitmq.producer.producer.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@EnableScheduling
@SpringBootApplication
public class Application implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    private static final List<String> SOURCES = List.of("mobile", "web");
    private static final List<String> TYPES = List.of("jpg", "png", "svg");

    private static final List<String> COLORS = List.of("white", "red", "green");
    private static final List<String> MATERIALS = List.of("wood", "plastic", "steel");

    @Autowired
    private HelloRabbitProducer helloRabbitProducer;

    @Autowired
    private EmployeeJsonProducer employeeJsonProducer;

    @Autowired
    private HumanResourceProducer humanResourceProducer;

    @Autowired
    private PictureProducer pictureProducer;

    @Autowired
    private PictureProducerTwo pictureProducerTwo;

    @Autowired
    private MyPictureProducer myPictureProducer;

    @Autowired
    private FurnitureProducer furnitureProducer;

    @Autowired
    private RetryPictureProducer retryPictureProducer;

    @Autowired
    private RetryEmployeeJsonProducer retryEmployeeJsonProducer;

    @Autowired
    private ReportRequestProducer reportRequestProducer;

    @Autowired
    private SpringPictureProducer springPictureProducer;

    @Autowired
    private SpringEmployeeJsonProducer springEmployeeJsonProducer;

    @Override
    public void run(String... args) throws Exception {
        //   helloRabbitProducer.sendHello("My name " + ThreadLocalRandom.current().nextInt());

        // employee JSON producer
//        for (int i = 0; i < 5; i++) {
//            var employee = new Employee("emp-" + i, "Nenad-" + i, LocalDate.now());
//            employeeJsonProducer.sendMessage(employee);
//        }
        // human resource producer - fanout exchange
//        for (int i = 0; i < 5; i++) {
//            var employee = new Employee("emp-" + i, "Nenad-" + i, LocalDate.now());
//            humanResourceProducer.sendMessage(employee);
//        }

        // direct exchange producer
//        for (int i = 0; i < 10; i++) {
//            var picture = new Picture(
//                    "name-" + i,
//                    TYPES.get(i % TYPES.size()),
//                    SOURCES.get(i % SOURCES.size()),
//                    i
//            );
//
//            pictureProducer.sendMessage(picture);
//        }

        // topic exchange producer
//        for (int i = 0; i <= 10; i++) {
//            var size = ThreadLocalRandom.current().nextInt(100, 10000);
//            var picture = new Picture(
//                    "name-" + i,
//                    TYPES.get(i % TYPES.size()),
//                    SOURCES.get(i % SOURCES.size()),
//                    size
//            );
//
//            pictureProducerTwo.sendMessage(picture);
//        }

        // error producer
//        for (int i = 0; i <= 10; i++) {
//            var size = ThreadLocalRandom.current().nextInt(9001, 10000);
//            var picture = new Picture(
//                    "name-" + i,
//                    TYPES.get(i % TYPES.size()),
//                    SOURCES.get(i % SOURCES.size()),
//                    size
//            );
//
//            myPictureProducer.sendMessage(picture);
//        }
        // header exchange
//        for (int i = 0; i < 10; i++) {
//            var furniture = new Furniture(
//                    "Furniture-" + i,
//                    COLORS.get(i % COLORS.size()),
//                    MATERIALS.get(i % MATERIALS.size()),
//                    i
//            );
//            System.out.printf("Producing %s\n", furniture);
//            furnitureProducer.sendMessage(furniture);
//        }

        // retry producer (direct exchange)
//        for (int i = 0; i < 10; i++) {
//            var picture = new Picture(
//                    "Picture-" + i,
//                    TYPES.get(i % TYPES.size()),
//                    SOURCES.get(i % SOURCES.size()),
//                    ThreadLocalRandom.current().nextInt(9000, 10000)
//            );
//            System.out.printf("Producing %s\n", picture);
//            retryPictureProducer.sendMessage(picture);
//        }
        // retry producer (fanout exchange)
//        for (int i = 0; i < 10; i++) {
//            var employee = new Employee("emp-" + i, null, LocalDate.now());
//            System.out.printf("Producing %s\n", employee);
//            retryEmployeeJsonProducer.sendMessage(employee);
//        }

        // delayed messages
//        boolean large = true;
//        for (int i = 0; i < 10; i++) {
//            var reportRequest = new ReportRequest("reportreq-" + i, large);
//            large = !large;
//            System.out.printf("Producing %s\n", reportRequest);
//            reportRequestProducer.sendMessage(reportRequest);
//        }

        // Spring retry - direct exchange
//        var picture = new Picture(
//                "Spring picture",
//                "jpg",
//                "web",
//                9500
//        );
//        System.out.printf("Producing %s\n", picture);
//        springPictureProducer.sendMessage(picture);

        // Spring retry - fanout exchange
        var employee = new Employee(

                "123",
                null,
                LocalDate.now()
        );
        System.out.printf("Producing %s\n", employee);
        springEmployeeJsonProducer.sendMessage(employee);

    }
}