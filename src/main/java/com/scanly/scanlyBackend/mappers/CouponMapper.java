package com.scanly.scanlyBackend.mappers;

import com.scanly.scanlyBackend.dtos.CouponResponse;
import com.scanly.scanlyBackend.models.Coupon;
import org.springframework.stereotype.Component;

@Component
public class CouponMapper {

    public CouponResponse mapToResponse(Coupon coupon) {
        return new CouponResponse(
                coupon.getId(),
                coupon.getCode(),
                coupon.getLabel(),
                coupon.getType(),
                coupon.getValue(),
                coupon.getMinOrderValue(),
                coupon.getActive(),
                coupon.getValidFrom(),
                coupon.getValidUntil(),
                coupon.getMaxUsages(),
                coupon.getCurrentUsages(),
                coupon.getCreatedAt()
        );
    }
}
