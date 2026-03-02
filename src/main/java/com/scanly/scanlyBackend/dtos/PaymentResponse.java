package com.scanly.scanlyBackend.dtos;

import com.scanly.scanlyBackend.models.enums.Currency;
import com.scanly.scanlyBackend.models.enums.PaymentStatus;

import java.math.BigDecimal;

public record PaymentResponse(
        Long paymentId,
        Long orderId,
        BigDecimal amount,
        String paymentMethod,
        Currency currency,
        PaymentStatus status
) {}
