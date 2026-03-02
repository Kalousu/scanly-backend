package com.scanly.scanlyBackend.dtos;

import java.math.BigDecimal;

public record OrderItemResponse (
    Long id,
    BigDecimal amount,
    String productName,
    BigDecimal unitPriceNet,
    BigDecimal taxRate,
    BigDecimal totalPriceGross
){}
