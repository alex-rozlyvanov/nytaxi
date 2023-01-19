package com.goal.taxi.front.controller;

import com.goal.taxi.common.dto.TotalDTO;
import com.goal.taxi.front.mapper.TotalRequestMapper;
import com.goal.taxi.front.model.TotalRequestParams;
import com.goal.taxi.front.service.TotalsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TotalsControllerTest {

    @Mock
    private TotalsService mockTotalsService;
    @Mock
    private TotalRequestMapper mockTotalRequestMapper;
    @InjectMocks
    private TotalsController controller;

    @Test
    void getTotals_callMap() {
        // GIVEN
        final var allParams = getTotalRequestParams();
        when(mockTotalRequestMapper.map(any())).thenReturn(Mono.empty());

        // WHEN
        final var mono = controller.getTotals(allParams);

        // THEN
        StepVerifier.create(mono).verifyComplete();
        verify(mockTotalRequestMapper).map(allParams);
    }

    private static Map<String, String> getTotalRequestParams() {
        return Map.of("test", "123");
    }

    @Test
    void getTotals_callGetTotals() {
        // GIVEN
        final var totalRequestParams = getTotalRequestParams(1);
        when(mockTotalRequestMapper.map(any())).thenReturn(Mono.just(totalRequestParams));
        when(mockTotalsService.getTotals(any())).thenReturn(Mono.just(buildTotalDTO(BigDecimal.TEN, LocalDate.now())));

        // WHEN
        final var mono = controller.getTotals(null);

        // THEN
        StepVerifier.create(mono).expectNextCount(1).verifyComplete();
        verify(mockTotalsService).getTotals(totalRequestParams);
    }

    @Test
    void getTotals_checkResult() {
        // GIVEN
        final var totalRequestParams = getTotalRequestParams(1);
        when(mockTotalRequestMapper.map(any())).thenReturn(Mono.just(totalRequestParams));

        final var totalDTO = buildTotalDTO(BigDecimal.ONE, LocalDate.now());
        when(mockTotalsService.getTotals(any())).thenReturn(Mono.just(totalDTO));

        // WHEN
        final var mono = controller.getTotals(null);

        // THEN
        StepVerifier.create(mono)
                .assertNext(result -> {
                    assertThat(result).isSameAs(totalDTO);
                })
                .verifyComplete();
    }

    private TotalRequestParams getTotalRequestParams(final int year) {
        return TotalRequestParams.builder()
                .year(year)
                .build();
    }

    private TotalDTO buildTotalDTO(BigDecimal total, LocalDate date) {
        return TotalDTO.builder()
                .total(total)
                .date(date)
                .build();
    }
}
