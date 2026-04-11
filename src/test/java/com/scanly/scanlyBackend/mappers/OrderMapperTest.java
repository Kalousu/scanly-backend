package com.scanly.scanlyBackend.mappers;

import com.scanly.scanlyBackend.dtos.OrderResponse;
import com.scanly.scanlyBackend.models.Order;
import com.scanly.scanlyBackend.models.OrderItem;
import com.scanly.scanlyBackend.models.Product;
import com.scanly.scanlyBackend.models.enums.OrderStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderMapperTest {

    private final OrderMapper mapper = new OrderMapper();

    @Test
    void testToOrderResponse() {
        Order order = new Order(OrderStatus.OPEN);
        order.setOrderId(1L);
        order.setItems(new ArrayList<>());
        
        Product product = new Product();
        product.setName("Coffee");
        
        OrderItem item = new OrderItem();
        item.setId(10L);
        item.setProduct(product);
        item.setAmount(new BigDecimal("2"));
        item.setUnitPrice(new BigDecimal("2.50"));
        item.setTaxRate(new BigDecimal("0.19"));
        item.setTotalPrice(new BigDecimal("5.95")); // (2 * 2.50) * 1.19 = 5.95
        order.getItems().add(item);

        OrderResponse response = mapper.toOrderResponse(order);

        assertEquals(1L, response.orderId());
        assertEquals(1, response.orderItems().size());
        assertEquals(0, new BigDecimal("5.95").compareTo(response.totalPrice()));
        assertEquals(OrderStatus.OPEN, response.orderStatus());
    }
}
