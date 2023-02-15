package com.goal.taxi.client.config;

import com.goal.taxi.client.service.FileProvider;
import com.goal.taxi.client.service.LocalFileProvider;
import com.goal.taxi.client.service.S3FileProvider;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class FileProviderConfig {
    private final LoadProperties loadProperties;

    @Bean
    @ConditionalOnProperty(name = "load.file-type", havingValue = "S3")
    FileProvider s3FileProvider () {
        return new S3FileProvider(loadProperties.getFilePath(), loadProperties.getFileBucket());
    }

    @Bean
    @ConditionalOnProperty(name = "load.file-type", havingValue = "LOCAL")
    FileProvider localFileProvider () {
        return new LocalFileProvider(loadProperties.getFilePath());
    }
}
