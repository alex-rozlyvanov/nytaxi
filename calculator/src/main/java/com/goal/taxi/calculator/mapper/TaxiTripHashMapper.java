package com.goal.taxi.calculator.mapper;

import com.goal.taxi.calculator.dao.entity.ReportView;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class TaxiTripHashMapper {

    public String getHash(final ReportView reportView) {
        return "%s-%s-%s".formatted(reportView.dropOffYear(), reportView.dropOffMonth(), reportView.dropOffDay());
    }

    public String getMonthHash(final LocalDate localDate) {
        return "%s-%s".formatted(localDate.getYear(), localDate.getMonth().getValue());
    }

}
