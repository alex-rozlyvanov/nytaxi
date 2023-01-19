package com.goal.taxi.front.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.goal.taxi.common.dto.TotalDTO;
import com.goal.taxi.front.dao.entity.Client;
import com.goal.taxi.front.dao.entity.RefreshToken;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfiguration {

    @Bean
    public ReactiveRedisTemplate<String, TotalDTO> redisTemplate(final ReactiveRedisConnectionFactory factory,
                                                                 final RedisSerializationContext<String, TotalDTO> context) {
        return new ReactiveRedisTemplate<>(factory, context);
    }

    @Bean
    public ReactiveRedisTemplate<String, Client> redisTemplateClient(final ReactiveRedisConnectionFactory factory,
                                                                     final RedisSerializationContext<String, Client> context) {
        return new ReactiveRedisTemplate<>(factory, context);
    }

    @Bean
    public ReactiveRedisTemplate<String, RefreshToken> redisTemplateGeneric(final ReactiveRedisConnectionFactory factory,
                                                                            final RedisSerializationContext<String, RefreshToken> context) {
        return new ReactiveRedisTemplate<>(factory, context);
    }

    @Bean
    public RedisSerializationContext<String, TotalDTO> redisTotalDTOSerializationContext() {
        final var hashKeySerializer = new StringRedisSerializer();
        final var hashValueSerializer = new Jackson2JsonRedisSerializer<>(TotalDTO.class);
        hashValueSerializer.setObjectMapper(redisObjectMapper());

        return RedisSerializationContext.<String, TotalDTO>newSerializationContext(new StringRedisSerializer())
                .hashKey(hashKeySerializer)
                .hashValue(hashValueSerializer)
                .build();
    }

    @Bean
    public RedisSerializationContext<String, Client> redisClientSerializationContext() {
        final var hashKeySerializer = new StringRedisSerializer();
        final var hashValueSerializer = new Jackson2JsonRedisSerializer<>(Client.class);
        hashValueSerializer.setObjectMapper(redisObjectMapper());

        return RedisSerializationContext.<String, Client>newSerializationContext(new StringRedisSerializer())
                .hashKey(hashKeySerializer)
                .hashValue(hashValueSerializer)
                .build();
    }

    @Bean
    public RedisSerializationContext<String, RefreshToken> redisRefreshTokenSerializationContext() {
        final var hashKeySerializer = new StringRedisSerializer();
        final var hashValueSerializer = new Jackson2JsonRedisSerializer<>(RefreshToken.class);
        hashValueSerializer.setObjectMapper(redisObjectMapper());

        return RedisSerializationContext.<String, RefreshToken>newSerializationContext(new StringRedisSerializer())
                .hashKey(hashKeySerializer)
                .hashValue(hashValueSerializer)
                .build();
    }

    @Bean
    public ObjectMapper redisObjectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }


}
