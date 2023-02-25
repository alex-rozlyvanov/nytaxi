package com.goal.taxi.client.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@AllArgsConstructor
public class LoadServiceConfig {

    private final LoadProperties loadProperties;

    @Bean
    ThreadPoolExecutor threadPoolExecutor() {
        return (ThreadPoolExecutor) Executors.newFixedThreadPool(loadProperties.getThreads());
    }

}
