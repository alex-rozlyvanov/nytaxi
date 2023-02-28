package com.goal.taxi.common.model;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Jacksonized
@SuppressWarnings("java:S116")
public class TaxiTrip {
    private final UUID id;
    private final Long vendorID;
    private final LocalDateTime pickUpDatetime;
    private final LocalDateTime dropOffDatetime;
    private final Long passengerCount;
    private final BigDecimal tripDistance;
    private final Long PULocationID;
    private final Long DOLocationID;
    private final Long ratecodeID;
    private final String storeAndForwardFlag;
    private final Long paymentType;
    private final BigDecimal fareAmount;
    private final BigDecimal extra;
    private final BigDecimal mtaTax;
    private final BigDecimal improvementSurcharge;
    private final BigDecimal tipAmount;
    private final BigDecimal tollsAmount;
    private final BigDecimal totalAmount;
}
