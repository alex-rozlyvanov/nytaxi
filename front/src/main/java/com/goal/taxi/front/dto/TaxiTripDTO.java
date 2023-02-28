package com.goal.taxi.front.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@Jacksonized
@SuppressWarnings("java:S116")
public class TaxiTripDTO {
    private UUID id;
    @JsonProperty("VendorID")
    @NotNull(message = "VendorID is required")
    private Long VendorID;
    @NotNull(message = "tpep_pickup_datetime is required")
    private String tpep_pickup_datetime;
    @NotNull(message = "tpep_dropoff_datetime is required")
    private String tpep_dropoff_datetime;
    @NotNull(message = "passenger_count is required")
    private Long passenger_count;
    @NotNull(message = "trip_distance is required")
    private BigDecimal trip_distance;
    @JsonProperty("PULocationID")
    @NotNull(message = "PULocationID is required")
    private Long PULocationID;
    @JsonProperty("DOLocationID")
    @NotNull(message = "DOLocationID is required")
    private Long DOLocationID;
    @JsonProperty("RatecodeID")
    @NotNull(message = "RatecodeID is required")
    private Long RatecodeID;
    @NotNull(message = "store_and_fwd_flag is required")
    private String store_and_fwd_flag;
    @NotNull(message = "payment_type is required")
    private Long payment_type;
    @NotNull(message = "fare_amount is required")
    private BigDecimal fare_amount;
    @NotNull(message = "extra is required")
    private BigDecimal extra;
    @NotNull(message = "mta_tax is required")
    private BigDecimal mta_tax;
    @NotNull(message = "improvement_surcharge is required")
    private BigDecimal improvement_surcharge;
    @NotNull(message = "tip_amount is required")
    private BigDecimal tip_amount;
    @NotNull(message = "tolls_amount is required")
    private BigDecimal tolls_amount;
    @NotNull(message = "total_amount is required")
    private BigDecimal total_amount;
}
