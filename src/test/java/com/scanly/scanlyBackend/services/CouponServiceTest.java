package com.scanly.scanlyBackend.services;

import com.scanly.scanlyBackend.dtos.CouponRequest;
import com.scanly.scanlyBackend.dtos.CouponResponse;
import com.scanly.scanlyBackend.dtos.CouponValidationResponse;
import com.scanly.scanlyBackend.exceptions.InvalidCouponException;
import com.scanly.scanlyBackend.mappers.CouponMapper;
import com.scanly.scanlyBackend.models.Coupon;
import com.scanly.scanlyBackend.models.enums.CouponType;
import com.scanly.scanlyBackend.repository.CouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private CouponMapper couponMapper;

    @InjectMocks
    private CouponService couponService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testValidateCouponNotFound() {
        when(couponRepository.findByCodeIgnoreCase("MISSING")).thenReturn(Optional.empty());
        
        CouponValidationResponse response = couponService.validateCoupon("MISSING", BigDecimal.TEN);
        
        assertFalse(response.valid());
        assertTrue(response.message().contains("nicht gefunden"));
    }

    @Test
    void testValidateCouponBelowMinOrderValue() {
        Coupon coupon = new Coupon("MIN50", "Save", CouponType.FIXED, BigDecimal.TEN, new BigDecimal("50.00"));
        coupon.setActive(true);
        when(couponRepository.findByCodeIgnoreCase("MIN50")).thenReturn(Optional.of(coupon));
        
        // Order value 30.00, min required 50.00
        CouponValidationResponse response = couponService.validateCoupon("MIN50", new BigDecimal("30.00"));
        
        assertFalse(response.valid());
        assertTrue(response.message().contains("Mindestbestellwert"));
    }

    @Test
    void testValidateCouponSuccess() {
        Coupon coupon = new Coupon("SAVE10", "Save 10", CouponType.FIXED, new BigDecimal("10.00"), new BigDecimal("20.00"));
        coupon.setActive(true);
        when(couponRepository.findByCodeIgnoreCase("SAVE10")).thenReturn(Optional.of(coupon));
        
        CouponResponse couponResponse = new CouponResponse(
            1L, "SAVE10", "Save 10", CouponType.FIXED, 
            new BigDecimal("10.00"), new BigDecimal("20.00"), 
            true, null, null, null, 0, null
        );
        when(couponMapper.mapToResponse(coupon)).thenReturn(couponResponse);
        
        CouponValidationResponse response = couponService.validateCoupon("SAVE10", new BigDecimal("50.00"));
        
        assertTrue(response.valid());
        assertEquals(0, new BigDecimal("10.00").compareTo(response.discount()));
        assertEquals(0, new BigDecimal("40.00").compareTo(response.totalAfterDiscount()));
    }

    @Test
    void testCreateDuplicateCoupon() {
        CouponRequest request = new CouponRequest("EXISTS", "label", CouponType.FIXED, BigDecimal.ONE, BigDecimal.ZERO, true, null, null, null);
        when(couponRepository.existsByCodeIgnoreCase("EXISTS")).thenReturn(true);
        
        assertThrows(InvalidCouponException.class, () -> couponService.createCoupon(request));
    }

    @Test
    void testCalculateDiscountPercentage() {
        Coupon coupon = new Coupon();
        coupon.setType(CouponType.PERCENTAGE);
        coupon.setValue(new BigDecimal("10.00")); // 10%
        
        BigDecimal subtotal = new BigDecimal("100.00");
        BigDecimal discount = couponService.calculateDiscount(coupon, subtotal);
        
        assertEquals(new BigDecimal("10.00").setScale(2, RoundingMode.HALF_UP), discount);
    }

    @Test
    void testCalculateDiscountFixed() {
        Coupon coupon = new Coupon();
        coupon.setType(CouponType.FIXED);
        coupon.setValue(new BigDecimal("5.00")); // 5 EUR
        
        BigDecimal subtotal = new BigDecimal("100.00");
        BigDecimal discount = couponService.calculateDiscount(coupon, subtotal);
        
        assertEquals(new BigDecimal("5.00").setScale(2, RoundingMode.HALF_UP), discount);
    }

    @Test
    void testCalculateDiscountFixedCappedBySubtotal() {
        Coupon coupon = new Coupon();
        coupon.setType(CouponType.FIXED);
        coupon.setValue(new BigDecimal("50.00")); // 50 EUR
        
        BigDecimal subtotal = new BigDecimal("30.00");
        BigDecimal discount = couponService.calculateDiscount(coupon, subtotal);
        
        assertEquals(new BigDecimal("30.00").setScale(2, RoundingMode.HALF_UP), discount);
    }

    @Test
    void testCalculateDiscountNull() {
        BigDecimal discount = couponService.calculateDiscount(null, new BigDecimal("100.00"));
        assertEquals(BigDecimal.ZERO, discount);
        
        discount = couponService.calculateDiscount(new Coupon(), null);
        assertEquals(BigDecimal.ZERO, discount);
    }

    @Test
    void testValidateCouponInactive() {
        Coupon coupon = new Coupon("INACTIVE", "Inactive", CouponType.FIXED, BigDecimal.TEN, BigDecimal.ZERO);
        coupon.setActive(false);
        when(couponRepository.findByCodeIgnoreCase("INACTIVE")).thenReturn(java.util.Optional.of(coupon));
        
        CouponValidationResponse response = couponService.validateCoupon("INACTIVE", new BigDecimal("50.00"));
        
        assertFalse(response.valid());
        assertTrue(response.message().contains("nicht mehr gueltig"));
    }

    @Test
    void testValidateCouponEmptyCode() {
        CouponValidationResponse response = couponService.validateCoupon("", new BigDecimal("50.00"));
        
        assertFalse(response.valid());
        assertTrue(response.message().contains("eingeben"));
    }

    @Test
    void testValidateCouponWhitespaceCode() {
        CouponValidationResponse response = couponService.validateCoupon("   ", new BigDecimal("50.00"));
        
        assertFalse(response.valid());
        assertTrue(response.message().contains("eingeben"));
    }

    @Test
    void testCalculateDiscountPercentageZeroSubtotal() {
        Coupon coupon = new Coupon();
        coupon.setType(CouponType.PERCENTAGE);
        coupon.setValue(new BigDecimal("10.00"));
        
        BigDecimal discount = couponService.calculateDiscount(coupon, BigDecimal.ZERO);
        
        assertEquals(BigDecimal.ZERO, discount);
    }

    @Test
    void testCalculateDiscountFixedExceedsSubtotal() {
        Coupon coupon = new Coupon();
        coupon.setType(CouponType.FIXED);
        coupon.setValue(new BigDecimal("100.00"));
        
        BigDecimal subtotal = new BigDecimal("50.00");
        BigDecimal discount = couponService.calculateDiscount(coupon, subtotal);
        
        assertEquals(new BigDecimal("50.00").setScale(2, RoundingMode.HALF_UP), discount);
    }

    @Test
    void testCreateCouponWithInvalidValue() {
        CouponRequest request = new CouponRequest(
            "INVALID", "label", CouponType.FIXED, 
            BigDecimal.ZERO, BigDecimal.ZERO, true, null, null, null
        );
        
        assertThrows(InvalidCouponException.class, () -> couponService.createCoupon(request));
    }

    @Test
    void testCreateCouponWithNegativeMinOrderValue() {
        CouponRequest request = new CouponRequest(
            "INVALID", "label", CouponType.FIXED, 
            BigDecimal.TEN, new BigDecimal("-10.00"), true, null, null, null
        );
        
        assertThrows(InvalidCouponException.class, () -> couponService.createCoupon(request));
    }

    @Test
    void testCreateCouponPercentageOver100() {
        CouponRequest request = new CouponRequest(
            "INVALID", "label", CouponType.PERCENTAGE, 
            new BigDecimal("150.00"), BigDecimal.ZERO, true, null, null, null
        );
        
        assertThrows(InvalidCouponException.class, () -> couponService.createCoupon(request));
    }
}
