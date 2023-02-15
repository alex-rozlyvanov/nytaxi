package com.goal.taxi.calculator.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.goal.taxi.common.dto.TotalDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfiguration {
    @Bean
    public RedisTemplate<String, TotalDTO> redisTemplate(final RedisConnectionFactory connectionFactory) {
        final var hashKeySerializer = new StringRedisSerializer();
        final var hashValueSerializer = new Jackson2JsonRedisSerializer<>(TotalDTO.class);
        final var objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        hashValueSerializer.setObjectMapper(objectMapper);

        final var redisTemplate = new RedisTemplate<String, TotalDTO>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setDefaultSerializer(hashKeySerializer);
        redisTemplate.setHashKeySerializer(hashKeySerializer);
        redisTemplate.setHashValueSerializer(hashValueSerializer);

        return redisTemplate;
    }

}
