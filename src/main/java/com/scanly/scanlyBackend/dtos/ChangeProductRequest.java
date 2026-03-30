package com.scanly.scanlyBackend.dtos;

import com.scanly.scanlyBackend.models.enums.PricingType;
import com.scanly.scanlyBackend.models.enums.ProductCategory;

import java.math.BigDecimal;

public record ChangeProductRequest(
        String code,
        String name,
        BigDecimal price,
        BigDecimal taxRate,
        ProductCategory productCategory
) {
}
