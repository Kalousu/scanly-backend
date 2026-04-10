package com.scanly.scanlyBackend.controllers;

import com.scanly.scanlyBackend.dtos.CouponRequest;
import com.scanly.scanlyBackend.dtos.CouponResponse;
import com.scanly.scanlyBackend.dtos.CouponValidationResponse;
import com.scanly.scanlyBackend.exceptions.CouponNotFoundException;
import com.scanly.scanlyBackend.exceptions.InvalidCouponException;
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
        try {
            return new ResponseEntity<>(couponService.getCouponById(id), HttpStatus.OK);
        } catch (CouponNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<CouponResponse> getCouponByCode(@PathVariable String code) {
        try {
            return new ResponseEntity<>(couponService.getCouponByCode(code), HttpStatus.OK);
        } catch (CouponNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
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
        try {
            return new ResponseEntity<>(couponService.createCoupon(request), HttpStatus.CREATED);
        } catch (InvalidCouponException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CouponResponse> updateCoupon(
            @PathVariable Long id,
            @RequestBody CouponRequest request
    ) {
        try {
            return new ResponseEntity<>(couponService.updateCoupon(id, request), HttpStatus.OK);
        } catch (CouponNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (InvalidCouponException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {
        try {
            couponService.deleteCoupon(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (CouponNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
