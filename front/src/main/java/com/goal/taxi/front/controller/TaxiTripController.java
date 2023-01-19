package com.goal.taxi.front.controller;

import com.goal.taxi.common.kafka.KafkaCustomHeaders;
import com.goal.taxi.front.dto.TaxiTripDTO;
import com.goal.taxi.front.service.TaxiTripKafkaService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/v1/message")
@AllArgsConstructor
public class TaxiTripController {
    private final TaxiTripKafkaService taxiTripKafkaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Void> storeTaxiTrip(@RequestHeader(KafkaCustomHeaders.IDEMPOTENCY_KEY) final String idempotencyKey,
                                    @Valid @RequestBody final TaxiTripDTO taxiTripDTO) {
        log.info("Send taxi trip to store topic");
        return taxiTripKafkaService.sendToStoreTopic(idempotencyKey, taxiTripDTO);
    }
}
