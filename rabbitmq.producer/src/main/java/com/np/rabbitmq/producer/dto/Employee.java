package com.np.rabbitmq.producer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record Employee(@JsonProperty("employee_id") String employeeId,
                       String name,
                       @JsonProperty("birth_date") @JsonFormat(pattern = "yyyy-MM-dd") LocalDate birthDate) {
}
