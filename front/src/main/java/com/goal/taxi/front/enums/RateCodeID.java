package com.goal.taxi.front.enums;

import com.goal.taxi.front.exception.RateCodeIDException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum RateCodeID {
    STANDARD_RATE(1L),
    JFK(2L),
    NEWARK(3L),
    NASSAU_OR_WESTCHESTER(4L),
    NEGOTIATED_FARE(5L),
    GROUP_RIDE(6L),
    VIP(99L); // ???

    private final Long id;

    public static RateCodeID getById(final Long id) {
        if (null == id) {
            throw new RateCodeIDException("Invalid RateCodeID: %d".formatted(id));
        }

        return Arrays.stream(RateCodeID.values())
                .filter(rc -> rc.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RateCodeIDException("Invalid RateCodeID: %d".formatted(id)));
    }
}
