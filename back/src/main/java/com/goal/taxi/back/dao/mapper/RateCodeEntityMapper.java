package com.goal.taxi.back.dao.mapper;

import com.goal.taxi.back.dao.entity.RateCodeEntity;
import org.springframework.stereotype.Component;

@Component
public class RateCodeEntityMapper {
    public RateCodeEntity mapFromRatecodeID(final Long ratecodeID) {
        return new RateCodeEntity().id(ratecodeID);
    }
}
