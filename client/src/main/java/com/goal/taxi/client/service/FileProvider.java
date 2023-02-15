package com.goal.taxi.client.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;

public interface FileProvider {
    BufferedReader getBufferedReader() throws FileNotFoundException;
}
