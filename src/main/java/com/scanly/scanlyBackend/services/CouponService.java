package com.scanly.scanlyBackend.services;

import com.scanly.scanlyBackend.dtos.CouponRequest;
import com.scanly.scanlyBackend.dtos.CouponResponse;
import com.scanly.scanlyBackend.dtos.CouponValidationResponse;
import com.scanly.scanlyBackend.exceptions.CouponNotFoundException;
import com.scanly.scanlyBackend.exceptions.InvalidCouponException;
import com.scanly.scanlyBackend.models.Coupon;
import com.scanly.scanlyBackend.models.enums.CouponType;
import com.scanly.scanlyBackend.repository.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class CouponService {

    @Autowired
    private CouponRepository couponRepository;

    public List<CouponResponse> getAllCoupons() {
        return couponRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    public CouponResponse getCouponById(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new CouponNotFoundException("Coupon with id " + id + " not found"));
        return mapToResponse(coupon);
    }

    public CouponResponse getCouponByCode(String code) {
        Coupon coupon = couponRepository.findByCodeIgnoreCase(code.trim())
                .orElseThrow(() -> new CouponNotFoundException("Coupon with code " + code + " not found"));
        return mapToResponse(coupon);
    }

    @Transactional
    public CouponResponse createCoupon(CouponRequest request) {
        if (couponRepository.existsByCodeIgnoreCase(request.code())) {
            throw new InvalidCouponException("Coupon with code " + request.code() + " already exists");
        }

        Coupon coupon = new Coupon();
        coupon.setCode(request.code().toUpperCase().trim());
        coupon.setLabel(request.label());
        coupon.setType(request.type());
        coupon.setValue(request.value());
        coupon.setMinOrderValue(request.minOrderValue());
        coupon.setActive(request.active() != null ? request.active() : true);
        coupon.setValidFrom(request.validFrom());
        coupon.setValidUntil(request.validUntil());
        coupon.setMaxUsages(request.maxUsages());
        coupon.setCurrentUsages(0);

        Coupon savedCoupon = couponRepository.save(coupon);
        return mapToResponse(savedCoupon);
    }

    @Transactional
    public CouponResponse updateCoupon(Long id, CouponRequest request) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new CouponNotFoundException("Coupon with id " + id + " not found"));

        // Check if code is being changed and if new code already exists
        if (!coupon.getCode().equalsIgnoreCase(request.code()) &&
                couponRepository.existsByCodeIgnoreCase(request.code())) {
            throw new InvalidCouponException("Coupon with code " + request.code() + " already exists");
        }

        coupon.setCode(request.code().toUpperCase().trim());
        coupon.setLabel(request.label());
        coupon.setType(request.type());
        coupon.setValue(request.value());
        coupon.setMinOrderValue(request.minOrderValue());
        coupon.setActive(request.active() != null ? request.active() : coupon.getActive());
        coupon.setValidFrom(request.validFrom());
        coupon.setValidUntil(request.validUntil());
        coupon.setMaxUsages(request.maxUsages());

        Coupon updatedCoupon = couponRepository.save(coupon);
        return mapToResponse(updatedCoupon);
    }

    @Transactional
    public void deleteCoupon(Long id) {
        if (!couponRepository.existsById(id)) {
            throw new CouponNotFoundException("Coupon with id " + id + " not found");
        }
        couponRepository.deleteById(id);
    }

    public CouponValidationResponse validateCoupon(String code, BigDecimal subtotal) {
        String normalizedCode = code.trim().toUpperCase();

        if (normalizedCode.isEmpty()) {
            return new CouponValidationResponse(
                    false,
                    "Bitte einen Coupon-Code eingeben.",
                    "",
                    null,
                    null,
                    null
            );
        }

        Coupon coupon = couponRepository.findByCodeIgnoreCase(normalizedCode)
                .orElse(null);

        if (coupon == null) {
            return new CouponValidationResponse(
                    false,
                    "Coupon nicht gefunden oder nicht mehr gueltig.",
                    normalizedCode,
                    null,
                    null,
                    null
            );
        }

        if (!coupon.isValid()) {
            return new CouponValidationResponse(
                    false,
                    "Coupon ist nicht mehr gueltig.",
                    normalizedCode,
                    null,
                    null,
                    null
            );
        }

        BigDecimal safeSubtotal = subtotal.max(BigDecimal.ZERO);

        if (safeSubtotal.compareTo(coupon.getMinOrderValue()) < 0) {
            return new CouponValidationResponse(
                    false,
                    String.format("Mindestbestellwert fuer diesen Coupon: %.2f EUR.", coupon.getMinOrderValue()),
                    normalizedCode,
                    null,
                    null,
                    null
            );
        }

        BigDecimal discount = calculateDiscount(coupon, safeSubtotal);
        BigDecimal totalAfterDiscount = safeSubtotal.subtract(discount).max(BigDecimal.ZERO);

        return new CouponValidationResponse(
                true,
                coupon.getLabel() + " aktiviert.",
                normalizedCode,
                mapToResponse(coupon),
                discount,
                totalAfterDiscount
        );
    }

    public BigDecimal calculateDiscount(Coupon coupon, BigDecimal subtotal) {
        if (coupon == null || subtotal == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal safeSubtotal = subtotal.max(BigDecimal.ZERO);

        if (safeSubtotal.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal discount;

        if (coupon.getType() == CouponType.PERCENTAGE) {
            discount = safeSubtotal.multiply(coupon.getValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else if (coupon.getType() == CouponType.FIXED) {
            discount = coupon.getValue().min(safeSubtotal);
        } else {
            discount = BigDecimal.ZERO;
        }

        return discount.setScale(2, RoundingMode.HALF_UP);
    }

    private CouponResponse mapToResponse(Coupon coupon) {
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
