package com.goal.taxi.front.enums;

import com.goal.taxi.front.exception.StoreAndForwardFlagException;
import org.springframework.util.StringUtils;

import java.util.Arrays;

public enum StoreAndForwardFlag {
    Y,
    N;

    public static StoreAndForwardFlag map(final String text) {
        if (!StringUtils.hasText(text)) {
            throw new StoreAndForwardFlagException("Invalid StoreAndForwardFlag id: %s".formatted(text));
        }

        return Arrays.stream(StoreAndForwardFlag.values())
                .filter(flag -> flag.name().equals(text))
                .findFirst()
                .orElseThrow(() -> new StoreAndForwardFlagException("Invalid StoreAndForwardFlag id: %s".formatted(text)));
    }
}
