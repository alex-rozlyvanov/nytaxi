package com.goal.taxi.front.service;

import com.goal.taxi.common.kafka.KafkaCustomHeaders;
import com.goal.taxi.common.model.TaxiTrip;
import com.goal.taxi.front.dto.TaxiTripDTO;
import com.goal.taxi.front.mapper.TaxiTripMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaxiTripKafkaService {
    private final TaxiTripMapper taxiTripMapper;
    private final ReactiveKafkaProducerTemplate<String, TaxiTrip> taxiTripKafkaTemplate;
    @Value("${app.kafka.topics.taxi-trip.name}")
    private String topicName;

    public Mono<Void> sendToStoreTopic(final String idempotencyKey,
                                       final TaxiTripDTO taxiTripDTO) {
        return taxiTripMapper.fromDTO(taxiTripDTO)
                .flatMap(taxiTrip -> send(idempotencyKey, taxiTrip));
    }

    private Mono<Void> send(final String idempotencyKey, final TaxiTrip taxiTrip) {
        return taxiTripKafkaTemplate.send(buildProducerRecord(idempotencyKey, taxiTrip))
                .doOnNext(r -> log.info("Record metadata: {} {}", r.recordMetadata().timestamp(), r.recordMetadata().toString()))
                .then();
    }

    private ProducerRecord<String, TaxiTrip> buildProducerRecord(final String idempotencyKey, final TaxiTrip taxiTrip) {
        final var producerRecord = new ProducerRecord<>(topicName, UUID.randomUUID().toString(), taxiTrip);
        producerRecord.headers().add(new RecordHeader(KafkaCustomHeaders.IDEMPOTENCY_KEY, idempotencyKey.getBytes()));

        return producerRecord;
    }
}
