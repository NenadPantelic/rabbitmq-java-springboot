package com.np.rabbitmq.producer.client;

import com.np.rabbitmq.producer.dto.RabbitmqQueue;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;
import java.util.List;

@Component
public class RabbitmqClient {

    public List<RabbitmqQueue> getAllQueues() {
        var webClient = WebClient.create("http://localhost:15672/api/queues");
        return webClient.get()
                .header(HttpHeaders.AUTHORIZATION, createBasicAuthHeaders())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<RabbitmqQueue>>() {
                }).block();
    }

    String createBasicAuthHeaders() {
        var auth = "guest:guest";
        return "Basic " + Base64.getEncoder().encodeToString(auth.getBytes());
    }
}
