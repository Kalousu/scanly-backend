package com.scanly.scanlyBackend.models;

import com.scanly.scanlyBackend.models.enums.CouponType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CouponTest {

    @Test
    void testIsValidActive() {
        Coupon coupon = new Coupon("C1", "Label", CouponType.FIXED, BigDecimal.TEN, BigDecimal.ZERO);
        coupon.setActive(true);
        assertTrue(coupon.isValid());

        coupon.setActive(false);
        assertFalse(coupon.isValid());
    }

    @Test
    void testIsValidDateRange() {
        Coupon coupon = new Coupon("C1", "Label", CouponType.FIXED, BigDecimal.TEN, BigDecimal.ZERO);
        Instant now = Instant.now();

        // Future start date
        coupon.setValidFrom(now.plus(1, ChronoUnit.DAYS));
        assertFalse(coupon.isValid(), "Should be invalid if start date is in the future");

        // Past end date
        coupon.setValidFrom(null);
        coupon.setValidUntil(now.minus(1, ChronoUnit.DAYS));
        assertFalse(coupon.isValid(), "Should be invalid if end date is in the past");

        // Valid range
        coupon.setValidFrom(now.minus(1, ChronoUnit.DAYS));
        coupon.setValidUntil(now.plus(1, ChronoUnit.DAYS));
        assertTrue(coupon.isValid(), "Should be valid within date range");
    }

    @Test
    void testIsValidUsageLimit() {
        Coupon coupon = new Coupon("C1", "Label", CouponType.FIXED, BigDecimal.TEN, BigDecimal.ZERO);
        coupon.setMaxUsages(5);
        coupon.setCurrentUsages(4);
        assertTrue(coupon.isValid());

        coupon.setCurrentUsages(5);
        assertFalse(coupon.isValid(), "Should be invalid if max usages reached");
    }
}
