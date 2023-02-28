package com.goal.taxi.back.dao.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Accessors(chain = true, fluent = true)
@Entity
@Table(name = "taxi_trip")
@SuppressWarnings("java:S116")
public class TaxiTripEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "vendor_id", referencedColumnName = "id")
    private VendorEntity vendor;

    @Column(name = "tpep_pick_up_datetime")
    private LocalDateTime pickUpDatetime;

    @Column(name = "tpep_dropoff_datetime")
    private LocalDateTime dropOffDatetime;

    @Column(name = "dropoff_day")
    private Short dropOffDay;

    @Column(name = "dropoff_month")
    private Short dropOffMonth;

    @Column(name = "dropoff_year")
    private Integer dropOffYear;

    @Column(name = "passenger_count")
    private Long passengerCount;

    @Column(name = "trip_distance")
    private BigDecimal tripDistance;

    @Column(name = "pulocationid")
    private Long PULocationID;

    @Column(name = "dolocationid")
    private Long DOLocationID;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "rate_code_id", referencedColumnName = "id")
    private RateCodeEntity ratecodeID;

    @Column(name = "store_and_forward_flag")
    private String storeAndForwardFlag;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "payment_type_id", referencedColumnName = "id")
    private PaymentTypeEntity paymentType;

    @Column(name = "fare_amount")
    private BigDecimal fareAmount;

    @Column(name = "extra")
    private BigDecimal extra;

    @Column(name = "mta_tax")
    private BigDecimal mtaTax;

    @Column(name = "improvement_surcharge")
    private BigDecimal improvementSurcharge;

    @Column(name = "tip_amount")
    private BigDecimal tipAmount;

    @Column(name = "tolls_amount")
    private BigDecimal tollsAmount;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;
}
