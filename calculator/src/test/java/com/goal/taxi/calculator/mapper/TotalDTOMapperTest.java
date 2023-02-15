package com.goal.taxi.calculator.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.of;

class TotalDTOMapperTest {

    @InjectMocks
    private TotalDTOMapper mapper;

    @BeforeEach
    public void before() {
        mapper = new TotalDTOMapper();
    }

    @Test
    void toTotalDTO_checkResult() {
        // GIVEN
        final var total = BigDecimal.valueOf(3123.34);
        final var epoch = LocalDate.EPOCH;

        // WHEN
        final var result = mapper.toTotalDTO(epoch, total);

        // THEN
        assertThat(result.getTotal()).isSameAs(total);
        assertThat(result.getDate()).isSameAs(epoch);
    }

    @ParameterizedTest
    @MethodSource("dayIsAbsentSource")
    @DisplayName("Day should be set to the last day of the month")
    void toMonthlyTotalDTO_checkResult(final short month, final short expectedDay) {
        // GIVEN
        final var total = BigDecimal.valueOf(3123.34);

        // WHEN
        final var result = mapper.toMonthlyTotalDTO(1111, month, total);

        // THEN
        assertThat(result.getTotal()).isEqualTo(total);
        assertThat(result.getDate().getDayOfMonth()).isEqualTo(expectedDay);
    }

    private static Stream<Arguments> dayIsAbsentSource() {
        return Stream.of(
                of((short) 10, (short) 31),
                of((short) 11, (short) 30),
                of((short) 12, (short) 31)
        );
    }

}
