package com.goal.taxi.back.service;

import com.goal.taxi.back.exceptions.DuplicateEventException;
import com.goal.taxi.back.exceptions.RetryableException;
import com.goal.taxi.common.kafka.KafkaCustomHeaders;
import com.goal.taxi.common.model.TaxiTrip;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class TaxiTripKafkaConsumer {
    private final IdempotentTaxiTripProcessor idempotentTaxiTripProcessor;

    @KafkaListener(
            topics = "${app.kafka.topics.taxi-trip.name}",
            concurrency = "${app.kafka.topics.taxi-trip.partitions}",
            containerFactory = "taxiTripKafkaListenerContainerFactory"
    )
    public void onMessage(@Header(KafkaCustomHeaders.IDEMPOTENCY_KEY) final String idempotencyKey,
                          final ConsumerRecord<String, TaxiTrip> consumerRecord) {
        log.info("Record idempotency-key '{}', offset '{}', partition '{}'", idempotencyKey, consumerRecord.offset(), consumerRecord.partition());
        try {
            idempotentTaxiTripProcessor.process(idempotencyKey, consumerRecord.value());
        } catch (DuplicateEventException e) {
            log.warn("Duplicate event received. Idempotency Key '{}'", idempotencyKey);
        } catch (RetryableException e) {
            log.debug("Throwing retryable exception.");
            throw e;
        }
    }
}
