package com.goal.taxi.front.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TotalRequestParams {
    private final Integer year;
    private final Short month;
    private final Short day;
}
