package com.goal.taxi.front.mapper;

import com.goal.taxi.common.model.TaxiTrip;
import com.goal.taxi.front.dto.TaxiTripDTO;
import com.goal.taxi.front.enums.PaymentType;
import com.goal.taxi.front.enums.RateCodeID;
import com.goal.taxi.front.enums.StoreAndForwardFlag;
import com.goal.taxi.front.exception.InvalidDataException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class TaxiTripMapper {
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a");

    public Mono<TaxiTrip> fromDTO(final TaxiTripDTO taxiTripDTO) {
        return Mono.fromSupplier(() -> buildTaxiTrip(taxiTripDTO));
    }

    private TaxiTrip buildTaxiTrip(final TaxiTripDTO taxiTripDTO) {
        return TaxiTrip.builder()
                .id(taxiTripDTO.getId())
                .vendorID(taxiTripDTO.getVendorID())
                .pickUpDatetime(parseLocalDateTime(taxiTripDTO.getTpep_pickup_datetime(), "pickUpDatetime"))
                .dropOffDatetime(parseLocalDateTime(taxiTripDTO.getTpep_dropoff_datetime(), "dropOffDatetime"))
                .passengerCount(taxiTripDTO.getPassenger_count())
                .tripDistance(taxiTripDTO.getTrip_distance())
                .PULocationID(taxiTripDTO.getPULocationID())
                .DOLocationID(taxiTripDTO.getDOLocationID())
                .ratecodeID(RateCodeID.getById(taxiTripDTO.getRatecodeID()).getId())
                .storeAndForwardFlag(StoreAndForwardFlag.map(taxiTripDTO.getStore_and_fwd_flag()).name())
                .paymentType(PaymentType.getById(taxiTripDTO.getPayment_type()).getId())
                .fareAmount(taxiTripDTO.getFare_amount())
                .extra(taxiTripDTO.getExtra())
                .mtaTax(taxiTripDTO.getMta_tax())
                .improvementSurcharge(taxiTripDTO.getImprovement_surcharge())
                .tipAmount(taxiTripDTO.getTip_amount())
                .tollsAmount(taxiTripDTO.getTolls_amount())
                .totalAmount(taxiTripDTO.getTotal_amount())
                .build();
    }

    private LocalDateTime parseLocalDateTime(final String text, final String field) {
        if (StringUtils.hasText(text)) {
            return LocalDateTime.parse(text, dateTimeFormatter);
        }
        throw new InvalidDataException("Field '%s' is required".formatted(field));
    }
}
