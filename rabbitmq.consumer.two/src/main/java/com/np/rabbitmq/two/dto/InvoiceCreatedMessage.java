package com.np.rabbitmq.two.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record InvoiceCreatedMessage(double amount,
                                    @JsonFormat(pattern = "yyyy-MM-dd") LocalDate createdDate,
                                    String currency,
                                    String invoiceNumber) {
}
