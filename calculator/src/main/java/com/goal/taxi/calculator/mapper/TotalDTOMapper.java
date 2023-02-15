package com.goal.taxi.calculator.mapper;

import com.goal.taxi.common.dto.TotalDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class TotalDTOMapper {

    public TotalDTO toTotalDTO(final LocalDate date, final BigDecimal total) {
        return TotalDTO.builder()
                .total(total)
                .date(date)
                .build();
    }

    public TotalDTO toMonthlyTotalDTO(final Integer year, final Short month, final BigDecimal total) {
        return TotalDTO.builder()
                .total(total)
                .date(LocalDate.of(year, month, mapDay(year, month)))
                .build();
    }

    private int mapDay(final int yearValue, final int monthValue) {
        return (short) LocalDate.of(yearValue, monthValue, 1).lengthOfMonth();
    }
}
