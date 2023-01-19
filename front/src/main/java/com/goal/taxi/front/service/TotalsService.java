package com.goal.taxi.front.service;

import com.goal.taxi.common.dto.TotalDTO;
import com.goal.taxi.front.mapper.TaxiTripHashMapper;
import com.goal.taxi.front.model.TotalRequestParams;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class TotalsService {
    private final TaxiTripHashMapper taxiTripHashMapper;
    private final ReactiveRedisOperations<String, TotalDTO> redisOperations;

    public Mono<TotalDTO> getTotals(final TotalRequestParams totalRequestParams) {

        final var hash = taxiTripHashMapper.fromTotalRequestParams(totalRequestParams);

        return redisOperations.opsForHash().get("Total", hash).map(v -> (TotalDTO) v);
    }

}
