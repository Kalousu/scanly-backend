package com.scanly.scanlyBackend.mappers;

import com.scanly.scanlyBackend.dtos.ReceiptResponse;
import com.scanly.scanlyBackend.dtos.ReceiptTaxGroupResponse;
import com.scanly.scanlyBackend.models.Order;
import com.scanly.scanlyBackend.models.OrderItem;
import com.scanly.scanlyBackend.models.Product;
import com.scanly.scanlyBackend.models.enums.OrderStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReceiptMapperTest {

    private final ReceiptMapper mapper = new ReceiptMapper();

    @Test
    void testToReceiptResponse() {
        Order order = new Order(OrderStatus.CLOSED);
        order.setOrderId(1L);
        order.setItems(new ArrayList<>());

        Product p1 = new Product();
        p1.setName("Milk");
        p1.setPricePerUnit(new BigDecimal("1.00"));
        p1.setTaxRate(new BigDecimal("0.07"));

        OrderItem i1 = new OrderItem(order, p1, new BigDecimal("2"), new BigDecimal("1.00"), new BigDecimal("0.07"));
        i1.setTotalPrice(new BigDecimal("2.14"));
        order.getItems().add(i1);

        Product p2 = new Product();
        p2.setName("Beer");
        p2.setPricePerUnit(new BigDecimal("2.00"));
        p2.setTaxRate(new BigDecimal("0.19"));

        OrderItem i2 = new OrderItem(order, p2, new BigDecimal("1"), new BigDecimal("2.00"), new BigDecimal("0.19"));
        i2.setTotalPrice(new BigDecimal("2.38"));
        order.getItems().add(i2);

        ReceiptResponse response = mapper.toReceiptResponse(order);

        assertEquals(1L, response.orderId());
        assertEquals(2, response.receiptItemResponseList().size());
        assertEquals(2, response.receiptTaxGroupResponseList().size());

        // Tax Group A (0.07)
        ReceiptTaxGroupResponse groupA = response.receiptTaxGroupResponseList().stream()
                .filter(g -> g.rate().compareTo(new BigDecimal("0.07")) == 0)
                .findFirst().get();
        assertEquals("A", groupA.label());
        assertEquals(0, new BigDecimal("2.00").compareTo(groupA.net()));
        assertEquals(0, new BigDecimal("0.14").compareTo(groupA.tax()));
        assertEquals(0, new BigDecimal("2.14").compareTo(groupA.gross()));

        // Tax Group B (0.19)
        ReceiptTaxGroupResponse groupB = response.receiptTaxGroupResponseList().stream()
                .filter(g -> g.rate().compareTo(new BigDecimal("0.19")) == 0)
                .findFirst().get();
        assertEquals("B", groupB.label());
        assertEquals(0, new BigDecimal("2.00").compareTo(groupB.net()));
        assertEquals(0, new BigDecimal("0.38").compareTo(groupB.tax()));
        assertEquals(0, new BigDecimal("2.38").compareTo(groupB.gross()));

        assertEquals(0, new BigDecimal("4.52").compareTo(response.totalAmount()));
    }
}
