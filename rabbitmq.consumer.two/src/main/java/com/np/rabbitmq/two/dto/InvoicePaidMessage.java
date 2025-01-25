package com.np.rabbitmq.two.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record InvoicePaidMessage(String invoiceNumber,
                                 @JsonFormat(pattern = "yyyy-MM-dd") LocalDate paidDate,
                                 String paymentNumber) {
}
