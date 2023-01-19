package com.goal.taxi.front.enums;

import com.goal.taxi.front.exception.PaymentTypeException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum PaymentType {
    CREDIT_CRD(1L),
    CASH(2L),
    NO_CHARGE(3L),
    DISPUTE(4L),
    UNKNOWN(5L),
    VOIDED_TRIP(6L);

    private final Long id;

    public static PaymentType getById(final Long id) {
        if (null == id) {
            throw new PaymentTypeException("Invalid PaymentType id: %d".formatted(id));
        }

        return Arrays.stream(PaymentType.values())
                .filter(rc -> rc.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new PaymentTypeException("Invalid PaymentType id: %d".formatted(id)));
    }
}
