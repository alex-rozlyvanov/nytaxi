package com.goal.taxi.client.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

@Slf4j
@AllArgsConstructor
public class LocalFileProvider implements FileProvider {
    private final String filePath;

    public BufferedReader getBufferedReader() throws FileNotFoundException {
        return new BufferedReader(new FileReader(filePath));
    }
}
