package com.scanly.scanlyBackend.dtos;

import com.scanly.scanlyBackend.models.enums.CouponType;

import java.math.BigDecimal;
import java.time.Instant;

public record CouponResponse(
        Long id,
        String code,
        String label,
        CouponType type,
        BigDecimal value,
        BigDecimal minOrderValue,
        Boolean active,
        Instant validFrom,
        Instant validUntil,
        Integer maxUsages,
        Integer currentUsages,
        Instant createdAt
) {
}
