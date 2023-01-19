package com.goal.taxi.front.dao.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@RedisHash("RefreshToken")
public class RefreshToken {
    private String clientId;
    private String token;
    private LocalDateTime expiryDate;
}
