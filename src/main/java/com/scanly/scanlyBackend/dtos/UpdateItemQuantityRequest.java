package com.scanly.scanlyBackend.dtos;

import java.math.BigDecimal;

public record UpdateItemQuantityRequest(
        BigDecimal delta
){}
