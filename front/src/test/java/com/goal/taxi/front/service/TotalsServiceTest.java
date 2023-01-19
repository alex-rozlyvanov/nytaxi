package com.goal.taxi.front.service;

import com.goal.taxi.common.dto.TotalDTO;
import com.goal.taxi.front.mapper.TaxiTripHashMapper;
import com.goal.taxi.front.model.TotalRequestParams;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.*;

@SuppressWarnings({"unchecked", "rawtypes"})
@ExtendWith(MockitoExtension.class)
class TotalsServiceTest {

    @Mock
    private TaxiTripHashMapper mockTaxiTripHashMapper;
    @Mock
    private ReactiveRedisOperations<String, TotalDTO> mockRedisOperations;

    @InjectMocks
    private TotalsService service;

    @Test
    void getTotals_callFromTotalRequestParams() {
        // GIVEN
        final ReactiveHashOperations mockReactiveHashOperations = mock(ReactiveHashOperations.class);
        when(mockRedisOperations.opsForHash()).thenReturn(mockReactiveHashOperations);
        when(mockReactiveHashOperations.get(anyString(), any())).thenReturn(Mono.empty());

        final var totalRequestParams = TotalRequestParams.builder()
                .year(0)
                .build();

        // WHEN
        final var mono = service.getTotals(totalRequestParams);

        // THEN
        StepVerifier.create(mono).verifyComplete();
        verify(mockTaxiTripHashMapper).fromTotalRequestParams(totalRequestParams);
    }

    @Test
    void getTotals_callGet() {
        // GIVEN
        final ReactiveHashOperations mockReactiveHashOperations = mock(ReactiveHashOperations.class);
        when(mockRedisOperations.opsForHash()).thenReturn(mockReactiveHashOperations);
        when(mockReactiveHashOperations.get(anyString(), any())).thenReturn(Mono.empty());

        when(mockTaxiTripHashMapper.fromTotalRequestParams(any())).thenReturn("test-hash");


        final var totalRequestParams = TotalRequestParams.builder()
                .year(0)
                .build();

        // WHEN
        final var mono = service.getTotals(totalRequestParams);

        // THEN
        StepVerifier.create(mono).verifyComplete();
        verify(mockReactiveHashOperations).get("Total", "test-hash");
    }

    @Test
    void getTotals_checkResult() {
        // GIVEN
        final ReactiveHashOperations mockReactiveHashOperations = mock(ReactiveHashOperations.class);
        when(mockRedisOperations.opsForHash()).thenReturn(mockReactiveHashOperations);

        final var totalDTO = getTotalDTO();
        when(mockReactiveHashOperations.get(anyString(), any())).thenReturn(Mono.just(totalDTO));

        when(mockTaxiTripHashMapper.fromTotalRequestParams(any())).thenReturn("test-hash");


        final var totalRequestParams = TotalRequestParams.builder()
                .year(0)
                .build();

        // WHEN
        final var mono = service.getTotals(totalRequestParams);

        // THEN
        StepVerifier.create(mono)
                .expectNext(totalDTO)
                .verifyComplete();
    }

    private static TotalDTO getTotalDTO() {
        return TotalDTO.builder()
                .total(BigDecimal.valueOf(14142.0))
                .date(LocalDate.EPOCH)
                .build();
    }
}
