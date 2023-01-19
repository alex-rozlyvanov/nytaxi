package com.goal.taxi.front.mapper;

import com.goal.taxi.front.dto.TaxiTripDTO;
import com.goal.taxi.front.enums.PaymentType;
import com.goal.taxi.front.enums.RateCodeID;
import com.goal.taxi.front.enums.StoreAndForwardFlag;
import com.goal.taxi.front.exception.InvalidDataException;
import com.goal.taxi.front.exception.PaymentTypeException;
import com.goal.taxi.front.exception.RateCodeIDException;
import com.goal.taxi.front.exception.StoreAndForwardFlagException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TaxiTripMapperTest {
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a");
    private final TaxiTripMapper service = new TaxiTripMapper();

    @Test
    void fromDTO_id_checkResult() {
        // GIVEN
        final var id = UUID.fromString("00000000-0000-0000-0000-00000000001");
        final var taxiTripDTO = getConfiguredBuilder().id(id).build();

        // WHEN
        final var mono = service.fromDTO(taxiTripDTO);

        // THEN
        StepVerifier.create(mono)
                .assertNext(result -> assertThat(result.getId()).isSameAs(id))
                .verifyComplete();
    }

    @Test
    void fromDTO_VendorID_checkResult() {
        // GIVEN
        final var taxiTripDTO = getConfiguredBuilder()
                .VendorID(15235L)
                .build();

        // WHEN
        final var mono = service.fromDTO(taxiTripDTO);

        // THEN
        StepVerifier.create(mono)
                .assertNext(result -> assertThat(result.getVendorID()).isEqualTo(15235L))
                .verifyComplete();
    }

    @Test
    void fromDTO_pickUpDatetime_checkResult() {
        // GIVEN
        final var taxiTripDTO = getConfiguredBuilder()
                .tpep_pickup_datetime("01/31/2018 06:32:24 PM")
                .build();

        // WHEN
        final var mono = service.fromDTO(taxiTripDTO);

        // THEN
        StepVerifier.create(mono)
                .assertNext(result -> assertThat(result.getPickUpDatetime())
                        .isEqualTo(LocalDateTime.parse("01/31/2018 06:32:24 PM", dateTimeFormatter)))
                .verifyComplete();
    }

    @Test
    void fromDTO_pickUpDatetimeIsNull_checkResult() {
        // GIVEN
        final var taxiTripDTO = getConfiguredBuilder()
                .tpep_pickup_datetime(null)
                .build();

        // WHEN
        final var mono = service.fromDTO(taxiTripDTO);

        // THEN
        StepVerifier.create(mono)
                .expectErrorSatisfies(expectedException -> assertThat(expectedException)
                        .isInstanceOf(InvalidDataException.class)
                        .hasMessage("400 BAD_REQUEST \"Field 'pickUpDatetime' is required\""))
                .verify();
    }

    @Test
    void fromDTO_dropOffDatetime_checkResult() {
        // GIVEN
        final var taxiTripDTO = getConfiguredBuilder()
                .tpep_dropoff_datetime("01/31/2018 06:42:24 PM")
                .build();

        // WHEN
        final var mono = service.fromDTO(taxiTripDTO);

        // THEN
        StepVerifier.create(mono)
                .assertNext(result -> assertThat(result.getDropOffDatetime())
                        .isEqualTo(LocalDateTime.parse("01/31/2018 06:42:24 PM", dateTimeFormatter)))
                .verifyComplete();
    }

    @Test
    void fromDTO_dropOffDatetimeIsNull_checkResult() {
        // GIVEN
        final var taxiTripDTO = getConfiguredBuilder()
                .tpep_dropoff_datetime(null)
                .build();

        // WHEN
        final var mono = service.fromDTO(taxiTripDTO);

        // THEN
        StepVerifier.create(mono)
                .expectErrorSatisfies(expectedException -> assertThat(expectedException)
                        .isInstanceOf(InvalidDataException.class)
                        .hasMessage("400 BAD_REQUEST \"Field 'dropOffDatetime' is required\""))
                .verify();
    }

    @Test
    void fromDTO_passengerCount_checkResult() {
        // GIVEN
        final var taxiTripDTO = getConfiguredBuilder()
                .passenger_count(42L)
                .build();

        // WHEN
        final var mono = service.fromDTO(taxiTripDTO);

        // THEN
        StepVerifier.create(mono)
                .assertNext(result -> assertThat(result.getPassengerCount()).isEqualTo(42L))
                .verifyComplete();
    }

    @Test
    void fromDTO_tripDistance_checkResult() {
        // GIVEN
        final var taxiTripDTO = getConfiguredBuilder()
                .trip_distance(BigDecimal.TEN)
                .build();

        // WHEN
        final var mono = service.fromDTO(taxiTripDTO);

        // THEN
        StepVerifier.create(mono)
                .assertNext(result -> assertThat(result.getTripDistance()).isEqualTo(BigDecimal.TEN))
                .verifyComplete();
    }

    @Test
    void fromDTO_PULocationID_checkResult() {
        // GIVEN
        final var taxiTripDTO = getConfiguredBuilder()
                .PULocationID(12L)
                .build();

        // WHEN
        final var mono = service.fromDTO(taxiTripDTO);

        // THEN
        StepVerifier.create(mono)
                .assertNext(result -> assertThat(result.getPULocationID()).isEqualTo(12L))
                .verifyComplete();
    }

    @Test
    void fromDTO_DOLocationID_checkResult() {
        // GIVEN
        final var taxiTripDTO = getConfiguredBuilder()
                .DOLocationID(11L)
                .build();

        // WHEN
        final var mono = service.fromDTO(taxiTripDTO);

        // THEN
        StepVerifier.create(mono)
                .assertNext(result -> assertThat(result.getDOLocationID()).isEqualTo(11L))
                .verifyComplete();
    }

    @Test
    void fromDTO_RatecodeID_checkResult() {
        // GIVEN
        final var taxiTripDTO = getConfiguredBuilder()
                .RatecodeID(RateCodeID.STANDARD_RATE.getId())
                .build();

        // WHEN
        final var mono = service.fromDTO(taxiTripDTO);

        // THEN
        StepVerifier.create(mono)
                .assertNext(result -> assertThat(result.getRatecodeID()).isEqualTo(1L))
                .verifyComplete();
    }

    @Test
    void fromDTO_RatecodeIDInvalid_checkResult() {
        // GIVEN
        final var taxiTripDTO = getConfiguredBuilder()
                .RatecodeID(-1L)
                .build();

        // WHEN
        final var mono = service.fromDTO(taxiTripDTO);

        // THEN
        StepVerifier.create(mono)
                .expectErrorSatisfies(expectedException -> assertThat(expectedException)
                        .isInstanceOf(RateCodeIDException.class)
                        .hasMessage("400 BAD_REQUEST \"Invalid RateCodeID: -1\""))
                .verify();
    }

    @Test
    void fromDTO_storeAndForwardFlag_checkResult() {
        // GIVEN
        final var taxiTripDTO = getConfiguredBuilder()
                .store_and_fwd_flag(StoreAndForwardFlag.Y.name())
                .build();

        // WHEN
        final var mono = service.fromDTO(taxiTripDTO);

        // THEN
        StepVerifier.create(mono)
                .assertNext(result -> assertThat(result.getStoreAndForwardFlag()).isSameAs("Y"))
                .verifyComplete();
    }

    @Test
    void fromDTO_storeAndForwardFlagInvalid_checkResult() {
        // GIVEN
        final var taxiTripDTO = getConfiguredBuilder()
                .store_and_fwd_flag("-1")
                .build();

        // WHEN
        final var mono = service.fromDTO(taxiTripDTO);

        // THEN
        StepVerifier.create(mono)
                .expectErrorSatisfies(expectedException -> assertThat(expectedException)
                        .isInstanceOf(StoreAndForwardFlagException.class)
                        .hasMessage("400 BAD_REQUEST \"Invalid StoreAndForwardFlag id: -1\""))
                .verify();
    }

    @Test
    void fromDTO_paymentType_checkResult() {
        // GIVEN
        final var taxiTripDTO = getConfiguredBuilder()
                .payment_type(PaymentType.CREDIT_CRD.getId())
                .build();

        // WHEN
        final var mono = service.fromDTO(taxiTripDTO);

        // THEN
        StepVerifier.create(mono)
                .assertNext(result -> assertThat(result.getPaymentType()).isSameAs(1L))
                .verifyComplete();
    }

    @Test
    void fromDTO_paymentTypeInvalid_checkResult() {
        // GIVEN
        final var taxiTripDTO = getConfiguredBuilder()
                .payment_type(-1L)
                .build();

        // WHEN
        final var mono = service.fromDTO(taxiTripDTO);

        // THEN
        StepVerifier.create(mono)
                .expectErrorSatisfies(expectedException -> assertThat(expectedException)
                        .isInstanceOf(PaymentTypeException.class)
                        .hasMessage("400 BAD_REQUEST \"Invalid PaymentType id: -1\""))
                .verify();
    }

    @Test
    void fromDTO_fareAmount_checkResult() {
        // GIVEN
        final var taxiTripDTO = getConfiguredBuilder()
                .fare_amount(BigDecimal.ONE)
                .build();

        // WHEN
        final var mono = service.fromDTO(taxiTripDTO);

        // THEN
        StepVerifier.create(mono)
                .assertNext(result -> assertThat(result.getFareAmount()).isSameAs(BigDecimal.ONE))
                .verifyComplete();
    }

    @Test
    void fromDTO_extra_checkResult() {
        // GIVEN
        final var taxiTripDTO = getConfiguredBuilder()
                .extra(BigDecimal.valueOf(768L))
                .build();

        // WHEN
        final var mono = service.fromDTO(taxiTripDTO);

        // THEN
        StepVerifier.create(mono)
                .assertNext(result -> assertThat(result.getExtra()).isEqualTo(BigDecimal.valueOf(768L)))
                .verifyComplete();
    }

    @Test
    void fromDTO_mtaTax_checkResult() {
        // GIVEN
        final var taxiTripDTO = getConfiguredBuilder()
                .mta_tax(BigDecimal.valueOf(432L))
                .build();

        // WHEN
        final var mono = service.fromDTO(taxiTripDTO);

        // THEN
        StepVerifier.create(mono)
                .assertNext(result -> assertThat(result.getMtaTax()).isEqualTo(BigDecimal.valueOf(432L)))
                .verifyComplete();
    }

    @Test
    void fromDTO_improvementSurcharge_checkResult() {
        // GIVEN
        final var taxiTripDTO = getConfiguredBuilder()
                .improvement_surcharge(BigDecimal.valueOf(123L))
                .build();

        // WHEN
        final var mono = service.fromDTO(taxiTripDTO);

        // THEN
        StepVerifier.create(mono)
                .assertNext(result -> assertThat(result.getImprovementSurcharge()).isEqualTo(BigDecimal.valueOf(123L)))
                .verifyComplete();
    }

    @Test
    void fromDTO_tipAmount_checkResult() {
        // GIVEN
        final var taxiTripDTO = getConfiguredBuilder()
                .tip_amount(BigDecimal.valueOf(1234L))
                .build();

        // WHEN
        final var mono = service.fromDTO(taxiTripDTO);

        // THEN
        StepVerifier.create(mono)
                .assertNext(result -> assertThat(result.getTipAmount()).isEqualTo(BigDecimal.valueOf(1234L)))
                .verifyComplete();
    }

    @Test
    void fromDTO_tollsAmount_checkResult() {
        // GIVEN
        final var taxiTripDTO = getConfiguredBuilder()
                .tolls_amount(BigDecimal.valueOf(45234L))
                .build();

        // WHEN
        final var mono = service.fromDTO(taxiTripDTO);

        // THEN
        StepVerifier.create(mono)
                .assertNext(result -> assertThat(result.getTollsAmount()).isEqualTo(BigDecimal.valueOf(45234L)))
                .verifyComplete();
    }

    @Test
    void fromDTO_totalAmount_checkResult() {
        // GIVEN
        final var taxiTripDTO = getConfiguredBuilder()
                .total_amount(BigDecimal.valueOf(434L))
                .build();

        // WHEN
        final var mono = service.fromDTO(taxiTripDTO);

        // THEN
        StepVerifier.create(mono)
                .assertNext(result -> assertThat(result.getTotalAmount()).isEqualTo(BigDecimal.valueOf(434L)))
                .verifyComplete();
    }

    private TaxiTripDTO.TaxiTripDTOBuilder getConfiguredBuilder() {
        return TaxiTripDTO.builder()
                .VendorID(1L)
                .tpep_pickup_datetime("01/31/2018 06:32:24 PM")
                .tpep_dropoff_datetime("01/31/2018 06:33:24 PM")
                .passenger_count(1L)
                .trip_distance(BigDecimal.TEN)
                .PULocationID(1L)
                .DOLocationID(1L)
                .RatecodeID(1L)
                .store_and_fwd_flag("Y")
                .payment_type(1L)
                .fare_amount(BigDecimal.TEN)
                .extra(BigDecimal.TEN)
                .mta_tax(BigDecimal.TEN)
                .improvement_surcharge(BigDecimal.TEN)
                .tip_amount(BigDecimal.TEN)
                .tolls_amount(BigDecimal.TEN)
                .total_amount(BigDecimal.TEN);
    }
}
