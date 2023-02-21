package com.goal.taxi.client.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goal.taxi.client.config.LoadProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoadService implements CommandLineRunner {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static Duration start;
    private final LoadProperties loadProperties;
    private final ShutdownManager shutdownManager;
    private final FileProvider fileProvider;
    private final ThreadPoolExecutor executorService;
    private final CreateEventTaskProvider createEventTaskProvider;
    private final GetEventTaskProvider getEventTaskProvider;

    @Override
    public void run(String... args) throws Exception {
        executorService.prestartAllCoreThreads();

        try (var bufferedReader = fileProvider.getBufferedReader()) {
            start = Duration.ofMillis(System.currentTimeMillis());
            final var end = start.plus(loadProperties.getDuration());
            final var names = bufferedReader.readLine().split(loadProperties.getSplitBy());
            var line = "";

            while ((line = bufferedReader.readLine()) != null && (recordsLimitNotExited() && notExpired(end))) {
                StatisticCollector.recordsCounter.incrementAndGet();
                final var taxiTripJson = toJsonObject(names, line.split(loadProperties.getSplitBy()));

                executorService.execute(createEventTaskProvider.getTask(taxiTripJson));
                executorService.execute(getEventTaskProvider.getTask(taxiTripJson));

                logMetaInfo();
                idle();
            }
        } finally {
            finalizeWork();
        }
        shutdownManager.initiateShutdown(0);
    }

    private void finalizeWork() throws InterruptedException {
        final var finish = Duration.ofMillis(System.currentTimeMillis()).minus(start);
        final var rate = getRequestsPerSecond(finish);
        final var formatted = DurationFormatUtils.formatDuration(finish.toMillis(), "HH:mm:ss.SSSS");

        executorService.shutdown();
        executorService.shutdownNow();
        log.info("Terminated gracefully: {}", executorService.awaitTermination(loadProperties.getGracefulShutdown().getSeconds(), TimeUnit.SECONDS));
        log.info("Done: {}, {} ms", formatted, finish.toMillis());
        log.info("Rows read: {}", StatisticCollector.recordsCounter.get());
        log.info("Sent records: {}", StatisticCollector.sentRecordsCounter.get());
        log.info("Errors: {}", StatisticCollector.errorsCounter.get());
        log.info("Rate: {} Requests/Second", rate);

    }

    private void idle() throws InterruptedException {
        if (executorService.getQueue().size() > loadProperties.getThrottle()) {
            log.info("Idle...");
            while (executorService.getQueue().size() > loadProperties.getThrottle() / 2) {
                Thread.sleep(1000);
            }
            log.info("Continue...");
        }
    }

    private void logMetaInfo() {
        if (StatisticCollector.recordsCounter.get() % 5000 == 0) {
            log.info("Pool size {}", executorService.getPoolSize());
            log.info("Active Threads {}", executorService.getActiveCount());
            log.info("Queued Tasks {}", executorService.getQueue().size());
        }
    }

    private BigDecimal getRequestsPerSecond(final Duration finish) {
        final var mathContext = new MathContext(5, RoundingMode.HALF_DOWN);
        final var durationInSeconds = BigDecimal.valueOf(TimeUnit.MILLISECONDS.convert(finish))
                .divide(BigDecimal.valueOf(1000), mathContext);

        return BigDecimal.valueOf(StatisticCollector.sentRecordsCounter.get())
                .divide(durationInSeconds, mathContext);
    }

    private boolean recordsLimitNotExited() {
        return loadProperties.getRecordLimits() == 0 || StatisticCollector.recordsCounter.get() < loadProperties.getRecordLimits();
    }

    private boolean notExpired(final Duration end) {
        return Duration.ofMillis(System.currentTimeMillis()).compareTo(end) < 0;
    }

    private String toJsonObject(final String[] fieldNames, final String[] line) throws JsonProcessingException {
        final Map<String, String> obj = new LinkedHashMap<>();
        for (int i = 0; i < fieldNames.length; i++) {
            obj.put(fieldNames[i], line[i]);
        }

        return objectMapper.writeValueAsString(obj);
    }

}
