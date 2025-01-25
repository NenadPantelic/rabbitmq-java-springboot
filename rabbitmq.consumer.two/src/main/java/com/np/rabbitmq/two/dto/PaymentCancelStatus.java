package com.np.rabbitmq.two.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record PaymentCancelStatus(boolean cancelStatus,
                                  @JsonFormat(pattern = "yyyy-MM-dd") LocalDate cancelDate,
                                  String invoiceNumber) {
}
