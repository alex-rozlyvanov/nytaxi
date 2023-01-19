package com.goal.taxi.front.service;

import com.goal.taxi.common.kafka.KafkaCustomHeaders;
import com.goal.taxi.common.model.TaxiTrip;
import com.goal.taxi.front.dto.TaxiTripDTO;
import com.goal.taxi.front.mapper.TaxiTripMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.SenderResult;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaxiTripKafkaServiceTest {
    @Captor
    private ArgumentCaptor<ProducerRecord<String, TaxiTrip>> producerRecordArgumentCaptor;
    @Mock
    private TaxiTripMapper mockTaxiTripMapper;
    @Mock
    private ReactiveKafkaProducerTemplate<String, TaxiTrip> mockTaxiTripKafkaTemplate;

    @InjectMocks
    private TaxiTripKafkaService service;

    @Test
    void sendToStoreTopic_callFromDTO() {
        // GIVEN
        final var taxiTripDTO = buildTaxiTripDTO();
        when(mockTaxiTripMapper.fromDTO(any())).thenReturn(Mono.empty());

        // WHEN
        final var mono = service.sendToStoreTopic(null, taxiTripDTO);

        // THEN
        StepVerifier.create(mono).expectNextCount(0).verifyComplete();
        verify(mockTaxiTripMapper).fromDTO(taxiTripDTO);
    }

    @SuppressWarnings("unchecked")
    @Test
    void sendToStoreTopic_callSend() {
        // GIVEN
        ReflectionTestUtils.setField(service, "topicName", "test-topic-name");

        final var taxiTrip = buildTaxiTrip();
        when(mockTaxiTripMapper.fromDTO(any())).thenReturn(Mono.just(taxiTrip));
        final var senderResult = buildSenderResult();
        when(mockTaxiTripKafkaTemplate.send(any(ProducerRecord.class)))
                .thenReturn(Mono.just(senderResult));

        // WHEN
        final var mono = service.sendToStoreTopic("idempotencyKey", null);

        // THEN
        StepVerifier.create(mono).expectNextCount(0).verifyComplete();
        verify(mockTaxiTripKafkaTemplate).send(producerRecordArgumentCaptor.capture());

        final var record = producerRecordArgumentCaptor.getValue();
        assertThat(record.value()).isSameAs(taxiTrip);
        final var idempotencyKeyBytes = record.headers()
                .lastHeader(KafkaCustomHeaders.IDEMPOTENCY_KEY)
                .value();
        assertThat(new String(idempotencyKeyBytes)).isEqualTo("idempotencyKey");
    }

    @SuppressWarnings("unchecked")
    private SenderResult<Void> buildSenderResult() {
        final var recordMetadata = buildRecordMetadata();
        final var senderResult = mock(SenderResult.class);
        when(senderResult.recordMetadata()).thenReturn(recordMetadata);

        return senderResult;
    }

    private RecordMetadata buildRecordMetadata() {
        return new RecordMetadata(
                new TopicPartition("test-topic-name", 1),
                10L, 1, 238472634,
                234234, 345345
        );
    }

    private TaxiTripDTO buildTaxiTripDTO() {
        final var id = UUID.fromString("00000000-0000-0000-0000-00000000001");

        return TaxiTripDTO.builder()
                .id(id)
                .build();
    }

    private TaxiTrip buildTaxiTrip() {
        final var id = UUID.fromString("00000000-0000-0000-0000-00000000001");

        return TaxiTrip.builder()
                .id(id)
                .build();
    }
}
