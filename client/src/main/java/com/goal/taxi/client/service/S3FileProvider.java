package com.goal.taxi.client.service;

import ch.qos.logback.core.util.FileUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;

@Slf4j
@AllArgsConstructor
public class S3FileProvider implements FileProvider {
    private static final S3Client s3 = S3Client.create();
    private static final String DEFAULT_PATH = "/tmp/s3";
    private final String key;
    private final String bucket;

    public BufferedReader getBufferedReader() throws FileNotFoundException {
        log.info("Downloading '{}' from S3 bucket '{}'...", key, bucket);

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        final var path = "%s/%s".formatted(DEFAULT_PATH, key);
        final var file = new File(path);
        FileUtil.createMissingParentDirectories(file);

        if (file.exists()) {
            log.info("File '{}' exists", path);
        } else {
            s3.getObject(getObjectRequest, ResponseTransformer.toFile(Path.of(path)));
            log.info("File '{}' downloaded", path);
        }

        return new BufferedReader(new FileReader(path));
    }

}
