package com.scanly.scanlyBackend.dtos;

import java.math.BigDecimal;

public record ProductResponse(
        String barcode,
        String name,
        BigDecimal price
) {}