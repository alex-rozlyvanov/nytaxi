package com.goal.taxi.back.service;

import com.goal.taxi.back.dao.entity.IdempotencyKey;
import com.goal.taxi.back.dao.repository.IdempotentKeyRepository;
import com.goal.taxi.back.exceptions.DuplicateEventException;
import com.goal.taxi.common.model.TaxiTrip;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IdempotentTaxiTripProcessorTest {

    @Mock
    private TaxiTripProcessor mockTaxiTripProcessor;
    @Mock
    private IdempotentKeyRepository mockIdempotentKeyRepository;

    @InjectMocks
    private IdempotentTaxiTripProcessor service;

    @Test
    void process_saveAndFlux() {
        // GIVEN

        // WHEN
        service.process("97d472f1-fee4-4848-9f8c-8038233b61a4", null);

        // THEN
        final var expected = new IdempotencyKey().id(UUID.fromString("97d472f1-fee4-4848-9f8c-8038233b61a4"));
        verify(mockIdempotentKeyRepository).saveAndFlush(expected);
    }

    @Test
    void process_DataIntegrityViolationExceptionThrown_throwDuplicateEventException() {
        // GIVEN
        final var dataIntegrityViolationException = new DataIntegrityViolationException("test message");
        when(mockIdempotentKeyRepository.saveAndFlush(any())).thenThrow(dataIntegrityViolationException);

        // WHEN
        final var expectedException = assertThrows(DuplicateEventException.class, () -> service.process("97d472f1-fee4-4848-9f8c-8038233b61a4", null));

        // THEN
        assertThat(expectedException)
                .hasCause(dataIntegrityViolationException)
                .hasMessage("Duplicated event processing occurred: 97d472f1-fee4-4848-9f8c-8038233b61a4");
    }

    @Test
    void process_callProcess() {
        // GIVEN
        final var id = UUID.fromString("00000000-fee4-0000-0000-8038233b61a4");
        final var taxiTrip = TaxiTrip.builder().id(id).build();

        // WHEN
        service.process("97d472f1-fee4-4848-9f8c-8038233b61a4", taxiTrip);

        // THEN
        final var inOrder = inOrder(mockIdempotentKeyRepository, mockTaxiTripProcessor);
        inOrder.verify(mockIdempotentKeyRepository).saveAndFlush(any());
        inOrder.verify(mockTaxiTripProcessor).process(taxiTrip);
    }
}
