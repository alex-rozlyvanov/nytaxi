package com.goal.taxi.front.mapper;

import com.goal.taxi.front.exception.TotalRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.of;

@ExtendWith(MockitoExtension.class)
class TotalRequestMapperTest {

    @InjectMocks
    private TotalRequestMapper mapper;

    @Test
    void map_checkResult() {
        // GIVEN
        final var params = Map.of(
                "year", "111",
                "month", "11",
                "day", "1"
        );

        // WHEN
        final var mono = mapper.map(params);

        // THEN
        StepVerifier.create(mono)
                .assertNext(result -> {
                    assertThat(result.getYear()).isEqualTo(111);
                    assertThat(result.getMonth()).isEqualTo((short) 11);
                    assertThat(result.getDay()).isEqualTo((short) 1);
                })
                .verifyComplete();
    }

    @ParameterizedTest
    @NullAndEmptySource
    void map_invalidDay_checkResult(final String day) {
        // GIVEN
        final var params = new HashMap<String, String>();
        params.put("year", "111");
        params.put("month", "11");
        params.put("day", day);

        // WHEN
        final var mono = mapper.map(params);

        // THEN
        StepVerifier.create(mono)
                .assertNext(result -> {
                    assertThat(result.getYear()).isEqualTo(111);
                    assertThat(result.getMonth()).isEqualTo((short) 11);
                    assertThat(result.getDay()).isEqualTo((short) 0);
                })
                .verifyComplete();
    }

    @ParameterizedTest
    @ValueSource(strings = {"year", "month", "day"})
    void map_allParamsAreMissing_checkResult(final String key) {
        // GIVEN
        final var params = Map.of(
                key, "test"
        );

        // WHEN
        final var mono = mapper.map(params);

        // THEN
        StepVerifier.create(mono)
                .expectErrorSatisfies(expectedException -> {
                    assertThat(expectedException)
                            .isInstanceOf(TotalRequestException.class)
                            .hasMessage("400 BAD_REQUEST \"Request parameters are missing - year, month, day(optional))\"");
                })
                .verify();
    }

    @ParameterizedTest
    @MethodSource("totalRequestParamsEmpty")
    void map_paramsAreEmpty_checkResult(final String year, final String yearV,
                                        final String month, final String monthV) {
        // GIVEN
        final var params = Map.of(
                year, yearV,
                month, monthV
        );

        // WHEN
        final var mono = mapper.map(params);

        // THEN
        StepVerifier.create(mono)
                .expectErrorSatisfies(expectedException -> {
                    assertThat(expectedException)
                            .isInstanceOf(TotalRequestException.class)
                            .hasMessage("400 BAD_REQUEST \"Request parameters are missing - year, month, day(optional))\"");
                })
                .verify();
    }

    private static Stream<Arguments> totalRequestParamsEmpty() {
        return Stream.of(
                of("year", "1", "month", ""),
                of("year", "", "month", "1"),
                of("year", "", "month", "")
        );
    }
}
