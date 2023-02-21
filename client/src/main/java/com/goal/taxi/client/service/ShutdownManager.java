package com.goal.taxi.client.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
class ShutdownManager {

    @Autowired
    private ApplicationContext appContext;

    public void initiateShutdown(int returnCode) {
        log.info("Exit {}", returnCode);
        System.exit(SpringApplication.exit(appContext, () -> returnCode));
    }
}
