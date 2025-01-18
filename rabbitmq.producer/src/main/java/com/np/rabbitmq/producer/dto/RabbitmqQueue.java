package com.np.rabbitmq.producer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RabbitmqQueue(long messages,
                            String name) {

    public boolean isDirty() {
        return messages != 0;
    }

}
