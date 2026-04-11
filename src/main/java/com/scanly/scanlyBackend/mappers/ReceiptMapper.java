package com.scanly.scanlyBackend.mappers;

import com.scanly.scanlyBackend.dtos.ReceiptItemResponse;
import com.scanly.scanlyBackend.dtos.ReceiptResponse;
import com.scanly.scanlyBackend.dtos.ReceiptTaxGroupResponse;
import com.scanly.scanlyBackend.models.Order;
import com.scanly.scanlyBackend.models.OrderItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReceiptMapper {
    public  ReceiptResponse toReceiptResponse(Order order){
        List<ReceiptItemResponse> items = order.getItems().stream()
                .map(this::toReceiptItemResponse)
                .toList();

        List<ReceiptTaxGroupResponse> taxGroups = toReceiptTaxGroupResponse(order);

        BigDecimal totalAmount = taxGroups.stream()
                .map(ReceiptTaxGroupResponse::gross)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        return new ReceiptResponse(
                order.getOrderId(),
                order.getCreationDate(),
                items,
                totalAmount,
                taxGroups
        );
    }

    public ReceiptItemResponse toReceiptItemResponse(OrderItem orderItem) {
        BigDecimal unitPriceGross = orderItem.getTotalPrice()
                .divide(orderItem.getAmount(), 2, RoundingMode.HALF_UP);

        return new ReceiptItemResponse(
                orderItem.getProduct().getName(),
                orderItem.getAmount(),
                orderItem.getUnitPrice(),
                unitPriceGross,
                orderItem.getTaxRate(),
                orderItem.getTotalPrice(),
                getTaxLabel(orderItem.getTaxRate())
        );
    }

    public List<ReceiptTaxGroupResponse> toReceiptTaxGroupResponse(Order order) {
        Map<BigDecimal, List<OrderItem>> groupedByTaxRate = order.getItems().stream()
                .collect(Collectors.groupingBy(OrderItem::getTaxRate));

        return groupedByTaxRate.entrySet().stream()
                .map(entry -> {
                    BigDecimal taxRate = entry.getKey();
                    List<OrderItem> items = entry.getValue();

                    BigDecimal netTotal = items.stream()
                            .map(item -> item.getUnitPrice().multiply(item.getAmount()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
                            .setScale(2, RoundingMode.HALF_UP);

                    BigDecimal grossTotal = netTotal.multiply(taxRate.add(BigDecimal.ONE))
                            .setScale(2, RoundingMode.HALF_UP);

                    BigDecimal taxAmount = grossTotal.subtract(netTotal)
                            .setScale(2, RoundingMode.HALF_UP);

                    return new ReceiptTaxGroupResponse(
                            getTaxLabel(taxRate),
                            taxRate,
                            netTotal,
                            taxAmount,
                            grossTotal
                    );
                })
                .sorted(Comparator.comparing(ReceiptTaxGroupResponse::rate))
                .toList();
    }

    public String getTaxLabel(BigDecimal taxRate){
        if(taxRate.compareTo(new BigDecimal("0.19")) == 0) {
            return "B";
        }else{
            return "A";
        }
    }
}
