package com.np.rabbitmq.consumer.dto;

public record ReportRequest(String reportName,
                            boolean large) {
}
