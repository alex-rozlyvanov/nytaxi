package com.goal.taxi.back.dao.mapper;

import com.goal.taxi.back.dao.entity.VendorEntity;
import org.springframework.stereotype.Component;

@Component
public class VendorEntityMapper {
    public VendorEntity mapFromVendorID(final Long vendorID) {
        return new VendorEntity().id(vendorID);
    }
}
