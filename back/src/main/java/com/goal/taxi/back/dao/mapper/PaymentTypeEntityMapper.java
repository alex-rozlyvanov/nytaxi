package com.goal.taxi.back.dao.mapper;

import com.goal.taxi.back.dao.entity.PaymentTypeEntity;
import org.springframework.stereotype.Component;

@Component
public class PaymentTypeEntityMapper {
    public PaymentTypeEntity mapFromPaymentType(final Long paymentType) {
        return new PaymentTypeEntity().id(paymentType);
    }
}
