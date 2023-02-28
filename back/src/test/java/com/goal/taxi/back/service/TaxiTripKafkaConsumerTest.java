package com.goal.taxi.back.service;

import com.goal.taxi.back.exceptions.DuplicateEventException;
import com.goal.taxi.back.exceptions.RetryableException;
import com.goal.taxi.common.model.TaxiTrip;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TaxiTripKafkaConsumerTest {

    @Mock
    private IdempotentTaxiTripProcessor mockIdempotentTaxiTripProcessor;
    @InjectMocks
    private TaxiTripKafkaConsumer service;

    @Test
    void onMessage_callProcess() {
        // GIVEN
        final var taxiTrip = buildTaxiTrip();
        final var consumerRecord = buildTaxiTripRecord(taxiTrip);

        // WHEN
        service.onMessage("idempotency-key", consumerRecord);

        // THEN
        verify(mockIdempotentTaxiTripProcessor).process("idempotency-key", taxiTrip);
    }

    @Test
    void onMessage_DuplicateEventExceptionThrown_proceedWithoutException() {
        // GIVEN
        final var taxiTrip = buildTaxiTrip();
        final var consumerRecord = buildTaxiTripRecord(taxiTrip);

        doThrow(DuplicateEventException.class).when(mockIdempotentTaxiTripProcessor).process(any(), any());

        // WHEN
        assertThatNoException()
                .isThrownBy(() -> service.onMessage("test-key", consumerRecord));

        // THEN
        System.out.println("Success");
    }

    @Test
    void onMessage_RetryableExceptionThrown_rethrowException() {
        // GIVEN
        final var taxiTrip = buildTaxiTrip();
        final var consumerRecord = buildTaxiTripRecord(taxiTrip);

        final var retryableException = new RetryableException("test message");
        doThrow(retryableException).when(mockIdempotentTaxiTripProcessor).process(any(), any());

        // WHEN
        final var expectedException = assertThrows(RetryableException.class, () -> service.onMessage("test-key", consumerRecord));

        // THEN
        assertThat(expectedException).isSameAs(retryableException);
    }

    private TaxiTrip buildTaxiTrip() {
        final var id = UUID.fromString("00000000-0000-0000-0000-00000000001");
        return TaxiTrip.builder().id(id).build();
    }

    private ConsumerRecord<String, TaxiTrip> buildTaxiTripRecord(final TaxiTrip taxiTrip) {
        return new ConsumerRecord<>(
                "topic",
                0,
                0,
                "key",
                taxiTrip
        );
    }
}
