package com.goal.taxi.back.service;

import com.goal.taxi.back.dao.mapper.TaxiTripEntityMapper;
import com.goal.taxi.back.dao.repository.PaymentTypeRepository;
import com.goal.taxi.back.dao.repository.RateCodeRepository;
import com.goal.taxi.back.dao.repository.TaxiTripRepository;
import com.goal.taxi.back.dao.repository.VendorRepository;
import com.goal.taxi.common.model.TaxiTrip;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class TaxiTripProcessor {
    private final TaxiTripRepository taxiTripRepository;
    private final VendorRepository vendorRepository;
    private final RateCodeRepository rateCodeRepository;
    private final PaymentTypeRepository paymentTypeRepository;
    private final TaxiTripEntityMapper taxiTripEntityMapper;

    @Transactional
    public TaxiTrip process(final TaxiTrip taxiTrip) {
        final var taxiTripEntity = taxiTripEntityMapper.mapFromTaxiTrip(taxiTrip);

        vendorRepository.findById(taxiTripEntity.vendor().id())
                .ifPresent(taxiTripEntity::vendor);
        rateCodeRepository.findById(taxiTripEntity.ratecodeID().id())
                .ifPresent(taxiTripEntity::ratecodeID);
        paymentTypeRepository.findById(taxiTripEntity.paymentType().id())
                .ifPresent(taxiTripEntity::paymentType);

        final var saved = taxiTripRepository.save(taxiTripEntity);
        return taxiTripEntityMapper.mapToTaxiTrip(saved);
    }
}
