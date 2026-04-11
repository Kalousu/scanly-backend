package com.scanly.scanlyBackend.controllers;

import com.scanly.scanlyBackend.dtos.CouponRequest;
import com.scanly.scanlyBackend.dtos.CouponResponse;
import com.scanly.scanlyBackend.dtos.CouponValidationResponse;
import com.scanly.scanlyBackend.services.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @GetMapping
    public ResponseEntity<List<CouponResponse>> getAllCoupons() {
        return new ResponseEntity<>(couponService.getAllCoupons(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CouponResponse> getCouponById(@PathVariable Long id) {
            return new ResponseEntity<>(couponService.getCouponById(id), HttpStatus.OK);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<CouponResponse> getCouponByCode(@PathVariable String code) {
            return new ResponseEntity<>(couponService.getCouponByCode(code), HttpStatus.OK);
    }

    @GetMapping("/validate/{code}")
    public ResponseEntity<CouponValidationResponse> validateCoupon(
            @PathVariable String code,
            @RequestParam BigDecimal subtotal
    ) {
        CouponValidationResponse response = couponService.validateCoupon(code, subtotal);
        return new ResponseEntity<>(response, response.valid() ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @PostMapping
    public ResponseEntity<CouponResponse> createCoupon(@RequestBody CouponRequest request) {
            return new ResponseEntity<>(couponService.createCoupon(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CouponResponse> updateCoupon(
            @PathVariable Long id,
            @RequestBody CouponRequest request
    ) {
            return new ResponseEntity<>(couponService.updateCoupon(id, request), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {
            couponService.deleteCoupon(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<CouponResponse> deactivateCoupon(@PathVariable Long id) {
            return new ResponseEntity<>(couponService.deactivateCoupon(id), HttpStatus.OK);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<CouponResponse> activateCoupon(@PathVariable Long id) {
            return new ResponseEntity<>(couponService.activateCoupon(id), HttpStatus.OK);
    }
}
