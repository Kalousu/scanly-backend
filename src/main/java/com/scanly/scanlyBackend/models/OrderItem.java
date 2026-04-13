package com.scanly.scanlyBackend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    @JsonIgnore
    @ManyToOne
    private Order order;
    @ManyToOne
    private Product product;

    private BigDecimal amount;
    private BigDecimal unitPrice;
    private BigDecimal taxRate;
    private BigDecimal totalPrice;

    public OrderItem(Order order, Product product, BigDecimal amount, BigDecimal unitPrice, BigDecimal taxRate) {
        this.order = order;
        this.product = product;
        this.amount = amount;
        this.unitPrice = unitPrice;
        this.taxRate = taxRate;
    }

    public BigDecimal calculateTotalPrice(BigDecimal amount, BigDecimal taxRate, BigDecimal unitPrice) {
        BigDecimal grossUnitPrice = unitPrice.multiply(taxRate.add(BigDecimal.ONE));
        return amount.multiply(grossUnitPrice).setScale(2, RoundingMode.HALF_UP);
    }
}
