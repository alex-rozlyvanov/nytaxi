package com.goal.taxi.front.mapper;

import com.goal.taxi.front.model.TotalRequestParams;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
public class TaxiTripHashMapper {
    public String fromTotalRequestParams(final TotalRequestParams totalRequestParams) {
        return isNull(totalRequestParams.getDay()) || totalRequestParams.getDay() == 0
                ? "%s-%s".formatted(totalRequestParams.getYear(), totalRequestParams.getMonth())
                : "%s-%s-%s".formatted(totalRequestParams.getYear(), totalRequestParams.getMonth(), totalRequestParams.getDay());
    }
}
