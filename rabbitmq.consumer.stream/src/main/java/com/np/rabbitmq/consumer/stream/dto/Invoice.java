package com.np.rabbitmq.consumer.stream.dto;

public record Invoice(String invoiceNumber,
                      InvoiceStatus status,
                      int amount) {
}
