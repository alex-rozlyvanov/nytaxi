package com.goal.taxi.front.mapper;

import com.goal.taxi.front.exception.TotalRequestException;
import com.goal.taxi.front.model.TotalRequestParams;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class TotalRequestMapper {
    private static final String YEAR = "year";
    private static final String MONTH = "month";
    private static final String DAY = "day";

    public Mono<TotalRequestParams> map(final Map<String, String> map) {
        return validate(map)
                .map(this::mapTo);
    }

    private Mono<Map<String, String>> validate(final Map<String, String> map) {
        if (!map.containsKey(YEAR) || !map.containsKey(MONTH)) {
            return Mono.error(new TotalRequestException("Request parameters are missing - year, month, day(optional))"));
        }
        if (!StringUtils.hasText(map.get(YEAR)) || !StringUtils.hasText(map.get(MONTH))) {
            return Mono.error(new TotalRequestException("Request parameters are missing - year, month, day(optional))"));
        }

        return Mono.just(map);
    }

    private TotalRequestParams mapTo(final Map<String, String> map) {
        final var yearValue = Integer.parseInt(map.get(YEAR));
        final var monthValue = Short.parseShort(map.get(MONTH));

        return TotalRequestParams.builder()
                .year(yearValue)
                .month(monthValue)
                .day(mapDay(map))
                .build();
    }

    private short mapDay(final Map<String, String> map) {
        final var dayValue = map.get(DAY);

        if (StringUtils.hasText(dayValue)) {
            return Short.parseShort(map.get(DAY));
        }

        return 0;
    }
}
