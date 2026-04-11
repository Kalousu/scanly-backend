package com.scanly.scanlyBackend.models;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderItemTest {

    @Test
    void testCalculateTotalPrice() {
        OrderItem item = new OrderItem();
        BigDecimal amount = new BigDecimal("2.0");
        BigDecimal unitPrice = new BigDecimal("10.0");
        BigDecimal taxRate = new BigDecimal("0.19");

        // (10.0 * (0.19 + 1.0)) * 2.0 = (10.0 * 1.19) * 2.0 = 11.9 * 2.0 = 23.8
        BigDecimal expected = new BigDecimal("23.800"); // Based on implementation: amount.multiply((unitPrice.multiply(taxRate.add(BigDecimal.ONE))))
        
        BigDecimal result = item.calculateTotalPrice(amount, taxRate, unitPrice);
        
        assertEquals(0, expected.compareTo(result), "Calculation logic should be correct.");
    }
}
