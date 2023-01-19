package com.goal.taxi.back.configuration;

import com.goal.taxi.back.exceptions.DuplicateEventException;
import com.goal.taxi.common.model.TaxiTrip;
import lombok.AllArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@AllArgsConstructor
public class KafkaConfiguration {
    private final KafkaProperties kafkaProperties;

    @Bean
    public ConsumerFactory<String, TaxiTrip> consumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        configProps.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        configProps.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getConsumer().getGroupId());
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    @Bean
    public ProducerFactory<String, TaxiTrip> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TaxiTrip> taxiTripKafkaListenerContainerFactory(final DefaultErrorHandler defaultErrorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, TaxiTrip> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setCommonErrorHandler(defaultErrorHandler);
        return factory;
    }

    @Bean
    public KafkaTemplate<String, TaxiTrip> kafkaTemplate(final ProducerFactory<String, TaxiTrip> factory) {
        return new KafkaTemplate<>(factory);
    }

    @Bean
    public DefaultErrorHandler defaultErrorHandler(final KafkaOperations<String, TaxiTrip> operations,
                                                   final KafkaCustomProperties kafkaCustomProperties) {
        // Publish to dead letter topic any messages dropped after retries with back off
        final var recoverer = new DeadLetterPublishingRecoverer(operations,
                // Always send to first/only partition of DLT suffixed topic
                (cr, e) -> new TopicPartition(cr.topic() + kafkaCustomProperties.getDeadLetterTopic().getSuffix(), 0));

        // Spread out attempts over time, taking a little longer between each attempt
        // Set a max for retries below max.poll.interval.ms; default: 5m, as otherwise we trigger a consumer rebalance
        final var backOff = kafkaCustomProperties.getBackOff();
        final var exponentialBackOff = new ExponentialBackOffWithMaxRetries(backOff.getMaxRetries());
        exponentialBackOff.setInitialInterval(backOff.getInitialInterval().toMillis());
        exponentialBackOff.setMultiplier(backOff.getMultiplier());
        exponentialBackOff.setMaxInterval(backOff.getMaxInterval().toMillis());

        final var errorHandler = new DefaultErrorHandler(recoverer, exponentialBackOff);
        errorHandler.addNotRetryableExceptions(DuplicateEventException.class);

        return errorHandler;
    }

}
