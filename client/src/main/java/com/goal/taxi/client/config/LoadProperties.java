package com.goal.taxi.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Data
@Component
@ConfigurationProperties(prefix = "load")
public class LoadProperties {
    private String splitBy = ",";
    private Integer threads = 24;
    private Long recordLimits = 1000L;
    private Integer throttle = 10_000;
    private Duration duration = Duration.ofSeconds(10);
    private String createUrl = "http://localhost:8080/api/v1/message";
    private String getUrl = "http://localhost:8080/api/v1/total?year=2018";
    private String filePath = "test.csv";
    private FileType fileType = FileType.LOCAL;
    private String fileBucket;
    private Duration gracefulShutdown = Duration.ofSeconds(10);
    private Request request = new Request();

    @Data
    public static class Request {
        private Duration timeout = Duration.ofSeconds(5);
    }

    public enum FileType {
        LOCAL, S3
    }
}
