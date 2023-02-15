package com.goal.taxi.calculator.service;

import com.goal.taxi.calculator.dao.entity.ReportView;
import com.goal.taxi.calculator.dao.repository.TaxiTripRepository;
import com.goal.taxi.calculator.mapper.TaxiTripHashMapper;
import com.goal.taxi.calculator.mapper.TotalDTOMapper;
import com.goal.taxi.common.dto.TotalDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Objects.isNull;

@Slf4j
@Component
@AllArgsConstructor
public class TotalsCalculator implements CommandLineRunner {
    private TaxiTripRepository taxiTripRepository;
    private TaxiTripHashMapper taxiTripHashMapper;
    private TotalDTOMapper totalDTOMapper;
    private RedisTemplate<String, TotalDTO> redisTemplate;

    @Override
    public void run(String... args) {
        log.info("Starting...");

        final var earliest = getEarliest();
        final var latest = getLatest();

        if (isNull(earliest) || isNull(latest)) {
            log.warn("Taxi Trips not found");
            return;
        }
        log.info("Earliest {}", earliest);
        log.info("Latest {}", latest);

        Stream.iterate(earliest, d -> d.plusMonths(1))
                .limit(earliest.datesUntil(latest, Period.ofMonths(1)).count() + 1)
                .parallel()
                .forEach(this::process);

        log.info("Done.");
    }

    private void process(final LocalDate localDate) {
        final var reportViewList = taxiTripRepository.getReportViewBy(localDate.getYear(), (short) localDate.getMonthValue());

        if (reportViewList.isEmpty()) {
            log.warn("{}-{} month has no events", localDate.getYear(), (short) localDate.getMonthValue());
            return;
        }

        processDailyTotals(reportViewList);
        processMonthlyTotals(reportViewList, localDate);
        log.info("{}-{} month. '{}' days were processed", localDate.getYear(), (short) localDate.getMonthValue(), reportViewList.size());
    }

    private void processDailyTotals(final List<ReportView> reportViewList) {
        reportViewList
                .stream().parallel()
                .forEach(i -> {
                    final var hash = taxiTripHashMapper.getHash(i);
                    final var date = LocalDate.of(i.dropOffYear(), i.dropOffMonth(), i.dropOffDay());
                    final var totalDTO = totalDTOMapper.toTotalDTO(date, i.totalAmount());
                    redisTemplate.opsForHash().put("Total", hash, totalDTO);
                });
    }

    private void processMonthlyTotals(final List<ReportView> reportViewList, final LocalDate localDate) {
        final var monthlyTotalAmount = aggregateMonthlyTotal(reportViewList);
        final var hash = taxiTripHashMapper.getMonthHash(localDate);
        final var totalDTO = totalDTOMapper.toMonthlyTotalDTO(localDate.getYear(), (short) localDate.getMonth().getValue(), monthlyTotalAmount);

        redisTemplate.opsForHash().put("Total", hash, totalDTO);
    }

    private BigDecimal aggregateMonthlyTotal(final List<ReportView> reportViewList) {
        return reportViewList
                .stream().parallel()
                .map(ReportView::totalAmount)
                .reduce(BigDecimal::add)
                .orElseThrow(() -> new RuntimeException("Unable to calculate monthly totals"));
    }

    private LocalDate getLatest() {
        final var pageable = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "dropOffDatetime"));
        return getDropOffDatetime(pageable);
    }

    private LocalDate getEarliest() {
        final var pageable = PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, "dropOffDatetime"));
        return getDropOffDatetime(pageable);
    }

    private LocalDate getDropOffDatetime(final Pageable pageable) {
        final var dropOffDatetime = taxiTripRepository.findDropOffDatetime(pageable);
        return dropOffDatetime.isEmpty() ? null : dropOffDatetime.getContent().get(0).toLocalDate();
    }

}
