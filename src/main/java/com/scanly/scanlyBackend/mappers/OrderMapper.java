package com.scanly.scanlyBackend.mappers;

import com.scanly.scanlyBackend.dtos.OrderItemResponse;
import com.scanly.scanlyBackend.dtos.OrderResponse;
import com.scanly.scanlyBackend.models.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class OrderMapper {
    public OrderResponse toOrderResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getId(),
                        item.getAmount(),
                        item.getProduct().getName(),
                        item.getUnitPrice(),
                        item.getTaxRate(),
                        item.getTotalPrice()
                )).toList();

        BigDecimal totalGross = itemResponses.stream()
                .map(OrderItemResponse::totalPriceGross)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new OrderResponse(
                order.getOrderId(),
                order.getCreationDate(),
                itemResponses,
                totalGross,
                order.getStatus()
        );
    }
}
