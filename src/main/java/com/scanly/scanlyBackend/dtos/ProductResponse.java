package com.scanly.scanlyBackend.dtos;

import com.scanly.scanlyBackend.models.enums.ProductCategory;

import java.math.BigDecimal;

public record ProductResponse(
        String code,
        String name,
        ProductCategory category,
        BigDecimal price
) {}