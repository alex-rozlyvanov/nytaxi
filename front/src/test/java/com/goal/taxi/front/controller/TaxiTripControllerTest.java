package com.goal.taxi.front.controller;

import com.goal.taxi.front.dto.TaxiTripDTO;
import com.goal.taxi.front.service.TaxiTripKafkaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaxiTripControllerTest {

    @Mock
    private TaxiTripKafkaService mockTaxiTripKafkaService;

    @InjectMocks
    private TaxiTripController service;

    @Test
    void createMessage_callSendToStoreTopic() {
        // GIVEN
        final var id = UUID.fromString("00000000-0000-0000-0000-00000000001");
        final var taxiTripDTO = TaxiTripDTO.builder().id(id).build();
        when(mockTaxiTripKafkaService.sendToStoreTopic(any(), any())).thenReturn(Mono.empty());

        // WHEN
        final var mono = service.storeTaxiTrip("idempotencyKey", taxiTripDTO);

        // THEN
        verify(mockTaxiTripKafkaService).sendToStoreTopic("idempotencyKey", taxiTripDTO);
        StepVerifier.create(mono).expectNextCount(0).verifyComplete();
    }
}
