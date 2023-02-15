package com.goal.taxi.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@Jacksonized
public class TotalDTO implements Serializable {
    private final BigDecimal total;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private final LocalDate date;
}
