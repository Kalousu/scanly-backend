package com.scanly.scanlyBackend.controllers;

import com.scanly.scanlyBackend.dtos.CouponRequest;
import com.scanly.scanlyBackend.dtos.CouponResponse;
import com.scanly.scanlyBackend.dtos.CouponValidationResponse;
import com.scanly.scanlyBackend.models.enums.CouponType;
import com.scanly.scanlyBackend.services.CouponService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CouponControllerTest {

    @Mock
    private CouponService couponService;

    @InjectMocks
    private CouponController couponController;

    private CouponResponse testCouponResponse;
    private CouponRequest testCouponRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        Instant now = Instant.now();
        Instant validUntil = now.plusSeconds(30 * 24 * 60 * 60); // 30 days
        
        testCouponResponse = new CouponResponse(
            1L,
            "TEST10",
            "Test Coupon",
            CouponType.PERCENTAGE,
            new BigDecimal("10.00"),
            new BigDecimal("50.00"),
            true,
            now,
            validUntil,
            10,
            0,
            now
        );

        testCouponRequest = new CouponRequest(
            "TEST10",
            "Test Coupon",
            CouponType.PERCENTAGE,
            new BigDecimal("10.00"),
            new BigDecimal("50.00"),
            true,
            now,
            validUntil,
            10
        );
    }

    @Test
    void getAllCoupons_ShouldReturnListOfCoupons() {
        List<CouponResponse> coupons = Arrays.asList(testCouponResponse);
        when(couponService.getAllCoupons()).thenReturn(coupons);

        ResponseEntity<List<CouponResponse>> response = couponController.getAllCoupons();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(couponService, times(1)).getAllCoupons();
    }

    @Test
    void getCouponById_ShouldReturnCoupon() {
        when(couponService.getCouponById(1L)).thenReturn(testCouponResponse);

        ResponseEntity<CouponResponse> response = couponController.getCouponById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("TEST10", response.getBody().code());
        verify(couponService, times(1)).getCouponById(1L);
    }

    @Test
    void getCouponByCode_ShouldReturnCoupon() {
        when(couponService.getCouponByCode("TEST10")).thenReturn(testCouponResponse);

        ResponseEntity<CouponResponse> response = couponController.getCouponByCode("TEST10");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("TEST10", response.getBody().code());
        verify(couponService, times(1)).getCouponByCode("TEST10");
    }

    @Test
    void validateCoupon_WhenValid_ShouldReturnOk() {
        CouponValidationResponse validResponse = new CouponValidationResponse(
            true,
            "Coupon is valid",
            "TEST10",
            testCouponResponse,
            new BigDecimal("10.00"),
            new BigDecimal("90.00")
        );
        when(couponService.validateCoupon(eq("TEST10"), any(BigDecimal.class)))
            .thenReturn(validResponse);

        ResponseEntity<CouponValidationResponse> response = 
            couponController.validateCoupon("TEST10", new BigDecimal("100.00"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().valid());
        verify(couponService, times(1)).validateCoupon(eq("TEST10"), any(BigDecimal.class));
    }

    @Test
    void validateCoupon_WhenInvalid_ShouldReturnBadRequest() {
        CouponValidationResponse invalidResponse = new CouponValidationResponse(
            false,
            "Coupon is invalid",
            "INVALID",
            null,
            BigDecimal.ZERO,
            new BigDecimal("100.00")
        );
        when(couponService.validateCoupon(eq("INVALID"), any(BigDecimal.class)))
            .thenReturn(invalidResponse);

        ResponseEntity<CouponValidationResponse> response = 
            couponController.validateCoupon("INVALID", new BigDecimal("100.00"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().valid());
        verify(couponService, times(1)).validateCoupon(eq("INVALID"), any(BigDecimal.class));
    }

    @Test
    void createCoupon_ShouldReturnCreatedCoupon() {
        when(couponService.createCoupon(any(CouponRequest.class))).thenReturn(testCouponResponse);

        ResponseEntity<CouponResponse> response = couponController.createCoupon(testCouponRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("TEST10", response.getBody().code());
        verify(couponService, times(1)).createCoupon(any(CouponRequest.class));
    }

    @Test
    void updateCoupon_ShouldReturnUpdatedCoupon() {
        when(couponService.updateCoupon(eq(1L), any(CouponRequest.class)))
            .thenReturn(testCouponResponse);

        ResponseEntity<CouponResponse> response = 
            couponController.updateCoupon(1L, testCouponRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(couponService, times(1)).updateCoupon(eq(1L), any(CouponRequest.class));
    }

    @Test
    void deleteCoupon_ShouldReturnNoContent() {
        doNothing().when(couponService).deleteCoupon(1L);

        ResponseEntity<Void> response = couponController.deleteCoupon(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(couponService, times(1)).deleteCoupon(1L);
    }

    @Test
    void deactivateCoupon_ShouldReturnDeactivatedCoupon() {
        Instant now = Instant.now();
        CouponResponse deactivatedCoupon = new CouponResponse(
            1L,
            "TEST10",
            "Test Coupon",
            CouponType.PERCENTAGE,
            new BigDecimal("10.00"),
            new BigDecimal("50.00"),
            false,
            now,
            now.plusSeconds(30 * 24 * 60 * 60),
            10,
            0,
            now
        );
        when(couponService.deactivateCoupon(1L)).thenReturn(deactivatedCoupon);

        ResponseEntity<CouponResponse> response = couponController.deactivateCoupon(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().active());
        verify(couponService, times(1)).deactivateCoupon(1L);
    }

    @Test
    void activateCoupon_ShouldReturnActivatedCoupon() {
        when(couponService.activateCoupon(1L)).thenReturn(testCouponResponse);

        ResponseEntity<CouponResponse> response = couponController.activateCoupon(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().active());
        verify(couponService, times(1)).activateCoupon(1L);
    }
}
