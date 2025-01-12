package com.np.rabbitmq.consumer.dto;

public record Picture(String name,
                      String type, // jpg, png or svg
                      String source,
                      long size) {
}
