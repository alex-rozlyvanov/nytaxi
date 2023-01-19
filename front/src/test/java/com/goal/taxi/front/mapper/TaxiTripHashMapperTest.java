package com.goal.taxi.front.mapper;

import com.goal.taxi.front.model.TotalRequestParams;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TaxiTripHashMapperTest {

    @Test
    void fromTotalRequestParams_month_checkResult() {
        // GIVEN
        final var requestParams = TotalRequestParams.builder()
                .year(123)
                .month((short) 12)
                .build();

        // WHEN
        final var result = new TaxiTripHashMapper().fromTotalRequestParams(requestParams);

        // THEN
        assertThat(result).isEqualTo("123-12");
    }

    @Test
    void fromTotalRequestParams_day_checkResult() {
        // GIVEN
        final var requestParams = TotalRequestParams.builder()
                .year(1234)
                .month((short) 11)
                .day((short) 1)
                .build();

        // WHEN
        final var result = new TaxiTripHashMapper().fromTotalRequestParams(requestParams);

        // THEN
        assertThat(result).isEqualTo("1234-11-1");
    }

}
