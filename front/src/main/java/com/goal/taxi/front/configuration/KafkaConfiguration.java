package com.goal.taxi.front.configuration;

import com.goal.taxi.common.model.TaxiTrip;
import lombok.AllArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import reactor.kafka.sender.SenderOptions;

import java.util.Properties;

@Configuration
@AllArgsConstructor
@ConditionalOnProperty("spring.kafka.bootstrap-servers")
public class KafkaConfiguration {

    private final KafkaProperties kafkaProperties;

    @Bean
    public ReactiveKafkaProducerTemplate<String, TaxiTrip> reactiveKafkaProducerTemplate() {
        final var properties = new Properties();
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        properties.put(ProducerConfig.RETRIES_CONFIG, "3");
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        final var senderOptions = SenderOptions.<String, TaxiTrip>create(properties);

        return new ReactiveKafkaProducerTemplate<>(senderOptions);
    }

    @Bean
    public NewTopic taxiTripTopic(@Value("${app.kafka.topics.taxi-trip.name}") final String topicName,
                                  @Value("${app.kafka.topics.taxi-trip.partitions}") final Integer partitions,
                                  @Value("${app.kafka.topics.taxi-trip.replicas}") final Integer replicas) {
        return TopicBuilder.name(topicName)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }

    @Bean
    public NewTopic taxiTripTopicDLT(@Value("${app.kafka.topics.taxi-trip.name}") final String topicName,
                                     @Value("${app.kafka.topics.taxi-trip.dead-letter-topic.partitions}") final Integer partitions,
                                     @Value("${app.kafka.dead-letter-topic.suffix}") final String deadLetterSuffix,
                                     @Value("${app.kafka.topics.taxi-trip.replicas}") final Integer replicas) {
        return TopicBuilder.name(topicName + deadLetterSuffix)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }
}
