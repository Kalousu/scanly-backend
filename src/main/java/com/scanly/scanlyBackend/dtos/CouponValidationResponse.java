package com.scanly.scanlyBackend.dtos;

import java.math.BigDecimal;

public record CouponValidationResponse(
        boolean valid,
        String message,
        String code,
        CouponResponse coupon,
        BigDecimal discount,
        BigDecimal totalAfterDiscount
) {
}
