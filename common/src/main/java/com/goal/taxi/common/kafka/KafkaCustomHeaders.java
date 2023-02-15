package com.goal.taxi.common.kafka;

import lombok.experimental.UtilityClass;

@UtilityClass
public class KafkaCustomHeaders {
    public static final String IDEMPOTENCY_KEY = "Idempotency-Key";
}
