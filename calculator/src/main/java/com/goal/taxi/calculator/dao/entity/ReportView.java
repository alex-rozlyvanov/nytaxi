package com.goal.taxi.calculator.dao.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(fluent = true)
@AllArgsConstructor
@NoArgsConstructor
public class ReportView {
    private Integer dropOffYear;
    private Short dropOffMonth;
    private Short dropOffDay;
    private BigDecimal totalAmount;
}
