package com.np.rabbitmq.two.dto;

public record DummyMessage(String content,
                           int publishOrder) {
}
