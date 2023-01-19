package com.goal.taxi.back.dao.mapper;

import com.goal.taxi.back.dao.entity.TaxiTripEntity;
import com.goal.taxi.common.model.TaxiTrip;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TaxiTripEntityMapper {
    private final VendorEntityMapper vendorEntityMapper;
    private final RateCodeEntityMapper rateCodeEntityMapper;
    private final PaymentTypeEntityMapper paymentTypeEntityMapper;

    public TaxiTripEntity mapFromTaxiTrip(final TaxiTrip taxiTrip) {
        return new TaxiTripEntity()
                .id(taxiTrip.getId())
                .vendor(vendorEntityMapper.mapFromVendorID(taxiTrip.getVendorID()))
                .pickUpDatetime(taxiTrip.getPickUpDatetime())
                .dropOffDatetime(taxiTrip.getDropOffDatetime())
                .dropOffDay((short) taxiTrip.getDropOffDatetime().getDayOfMonth())
                .dropOffMonth((short) taxiTrip.getDropOffDatetime().getMonthValue())
                .dropOffYear(taxiTrip.getDropOffDatetime().getYear())
                .passengerCount(taxiTrip.getPassengerCount())
                .tripDistance(taxiTrip.getTripDistance())
                .PULocationID(taxiTrip.getPULocationID())
                .DOLocationID(taxiTrip.getDOLocationID())
                .ratecodeID(rateCodeEntityMapper.mapFromRatecodeID(taxiTrip.getRatecodeID()))
                .storeAndForwardFlag(taxiTrip.getStoreAndForwardFlag())
                .paymentType(paymentTypeEntityMapper.mapFromPaymentType(taxiTrip.getPaymentType()))
                .fareAmount(taxiTrip.getFareAmount())
                .extra(taxiTrip.getExtra())
                .mtaTax(taxiTrip.getMtaTax())
                .improvementSurcharge(taxiTrip.getImprovementSurcharge())
                .tipAmount(taxiTrip.getTipAmount())
                .tollsAmount(taxiTrip.getTollsAmount())
                .totalAmount(taxiTrip.getTotalAmount());
    }

    public TaxiTrip mapToTaxiTrip(final TaxiTripEntity taxiTripEntity) {
        return TaxiTrip.builder()
                .id(taxiTripEntity.id())
                .vendorID(taxiTripEntity.vendor().id())
                .pickUpDatetime(taxiTripEntity.pickUpDatetime())
                .dropOffDatetime(taxiTripEntity.dropOffDatetime())
                .passengerCount(taxiTripEntity.passengerCount())
                .tripDistance(taxiTripEntity.tripDistance())
                .PULocationID(taxiTripEntity.PULocationID())
                .DOLocationID(taxiTripEntity.DOLocationID())
                .ratecodeID(taxiTripEntity.ratecodeID().id())
                .storeAndForwardFlag(taxiTripEntity.storeAndForwardFlag())
                .paymentType(taxiTripEntity.paymentType().id())
                .fareAmount(taxiTripEntity.fareAmount())
                .extra(taxiTripEntity.extra())
                .mtaTax(taxiTripEntity.mtaTax())
                .improvementSurcharge(taxiTripEntity.improvementSurcharge())
                .tipAmount(taxiTripEntity.tipAmount())
                .tollsAmount(taxiTripEntity.tollsAmount())
                .totalAmount(taxiTripEntity.totalAmount())
                .build();
    }
}
