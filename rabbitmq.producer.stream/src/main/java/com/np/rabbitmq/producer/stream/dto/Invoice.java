package com.np.rabbitmq.producer.stream.dto;

public record Invoice(String invoiceNumber,
                      InvoiceStatus status,
                      int amount) {
}
