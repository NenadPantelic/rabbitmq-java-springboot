package com.np.rabbitmq.producer.dto;

public record ReportRequest(String reportName,
                            boolean large) {
}
