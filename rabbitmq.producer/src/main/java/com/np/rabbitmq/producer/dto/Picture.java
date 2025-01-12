package com.np.rabbitmq.producer.dto;

public record Picture(String name,
                      String type, // jpg, png or svg
                      String source,
                      long size) {
}
