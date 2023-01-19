package com.goal.taxi.back.configuration;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Data
@Component
@ConfigurationProperties(prefix = "app.kafka")
public class KafkaCustomProperties {
    private BackOff backOff;
    private DeadLetterTopic deadLetterTopic;

    @Data
    public static class BackOff {
        private Integer maxRetries = 10;
        private Duration initialInterval = Duration.ofMillis(500);
        private Duration maxInterval = Duration.ofSeconds(5);
        private Double multiplier = 1.5d;
    }

    @Data
    public static class DeadLetterTopic {
        private String suffix = ".DLT";
    }
}
