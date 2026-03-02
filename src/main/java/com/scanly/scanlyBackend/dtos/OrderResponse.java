package com.scanly.scanlyBackend.dtos;

import com.scanly.scanlyBackend.models.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse (
    Long orderId,
    Instant creationDate,
    List<OrderItemResponse> orderItems,
    BigDecimal totalPrice,
    OrderStatus orderStatus
    ){}
