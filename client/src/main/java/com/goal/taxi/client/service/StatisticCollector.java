package com.goal.taxi.client.service;

import lombok.experimental.UtilityClass;

import java.util.concurrent.atomic.AtomicLong;

@UtilityClass
public class StatisticCollector {
    public static final AtomicLong recordsCounter = new AtomicLong();
    public static final AtomicLong sentRecordsCounter = new AtomicLong();
    public static final AtomicLong errorsCounter = new AtomicLong();
}
