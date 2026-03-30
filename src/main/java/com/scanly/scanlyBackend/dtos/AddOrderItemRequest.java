package com.scanly.scanlyBackend.dtos;

import java.math.BigDecimal;

public record AddOrderItemRequest (
        String code,
        BigDecimal amount
){}
