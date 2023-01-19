package com.goal.taxi.back.service;

import com.goal.taxi.back.dao.entity.PaymentTypeEntity;
import com.goal.taxi.back.dao.entity.RateCodeEntity;
import com.goal.taxi.back.dao.entity.TaxiTripEntity;
import com.goal.taxi.back.dao.entity.VendorEntity;
import com.goal.taxi.back.dao.mapper.TaxiTripEntityMapper;
import com.goal.taxi.back.dao.repository.PaymentTypeRepository;
import com.goal.taxi.back.dao.repository.RateCodeRepository;
import com.goal.taxi.back.dao.repository.TaxiTripRepository;
import com.goal.taxi.back.dao.repository.VendorRepository;
import com.goal.taxi.common.model.TaxiTrip;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaxiTripProcessorTest {
    @Mock
    private TaxiTripRepository mockTaxiTripRepository;
    @Mock
    private TaxiTripEntityMapper mockTaxiTripEntityMapper;
    @Mock
    private VendorRepository mockVendorRepository;
    @Mock
    private RateCodeRepository mockRateCodeRepository;
    @Mock
    private PaymentTypeRepository mockPaymentTypeRepository;

    @InjectMocks
    private TaxiTripProcessor service;

    @Test
    void process_callFromTaxiTrip() {
        // GIVEN
        final var taxiTrip = buildTaxiTrip();
        when(mockTaxiTripEntityMapper.mapFromTaxiTrip(any())).thenReturn(buildTaxiTripEntity());

        // WHEN
        service.process(taxiTrip);

        // THEN
        verify(mockTaxiTripEntityMapper).mapFromTaxiTrip(taxiTrip);
    }

    @Test
    void process_callSave() {
        // GIVEN
        final var taxiTripEntity = buildTaxiTripEntity();
        when(mockTaxiTripEntityMapper.mapFromTaxiTrip(any())).thenReturn(taxiTripEntity);

        // WHEN
        service.process(null);

        // THEN
        verify(mockTaxiTripRepository).save(taxiTripEntity);
    }

    @Test
    void process_callMapToTaxiTrip() {
        // GIVEN
        when(mockTaxiTripEntityMapper.mapFromTaxiTrip(any())).thenReturn(buildTaxiTripEntity());
        final var taxiTripEntity = buildTaxiTripEntity();
        when(mockTaxiTripRepository.save(any())).thenReturn(taxiTripEntity);

        // WHEN
        service.process(null);

        // THEN
        verify(mockTaxiTripEntityMapper).mapToTaxiTrip(taxiTripEntity);
    }

    @Test
    void process_checkResult() {
        // GIVEN
        when(mockTaxiTripEntityMapper.mapFromTaxiTrip(any())).thenReturn(buildTaxiTripEntity());
        final var taxiTrip = buildTaxiTrip();
        when(mockTaxiTripEntityMapper.mapToTaxiTrip(any())).thenReturn(taxiTrip);

        // WHEN
        final var result = service.process(taxiTrip);

        // THEN
        assertThat(result).isSameAs(taxiTrip);
    }

    private TaxiTrip buildTaxiTrip() {
        final var id = UUID.fromString("00000000-0000-0000-0000-00000000001");

        return TaxiTrip.builder()
                .id(id)
                .build();
    }

    private static TaxiTripEntity buildTaxiTripEntity() {
        final var id = UUID.fromString("00000000-0000-0000-0000-00000000001");

        return new TaxiTripEntity()
                .id(id)
                .vendor(new VendorEntity())
                .paymentType(new PaymentTypeEntity())
                .ratecodeID(new RateCodeEntity());
    }

}
