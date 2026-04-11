package com.scanly.scanlyBackend;

import com.scanly.scanlyBackend.dtos.AddOrderItemRequest;
import com.scanly.scanlyBackend.dtos.ApplyCouponRequest;
import com.scanly.scanlyBackend.dtos.OrderResponse;
import com.scanly.scanlyBackend.models.Order;
import com.scanly.scanlyBackend.repository.OrderRepository;
import com.scanly.scanlyBackend.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderFlowIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void testFullOrderFlow() throws Exception {
        // 1. Create Order
        MvcResult createResult = mockMvc.perform(post("/api/orders"))
                .andExpect(status().isCreated())
                .andReturn();
        
        Long orderId = Long.parseLong(createResult.getResponse().getContentAsString());
        assertNotNull(orderId);

        // 2. Add Product (using seeded barcode from DataSeeder)
        // Adding 10 Monster Whites to definitely exceed any min order value (2.49 * 10 = 24.90)
        AddOrderItemRequest addRequest = new AddOrderItemRequest("1234567890123", new BigDecimal("10.0"));
        mockMvc.perform(post("/api/orders/" + orderId + "/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isOk());

        // 3. Get Order and verify
        MvcResult getResult = mockMvc.perform(get("/api/orders/" + orderId))
                .andExpect(status().isOk())
                .andReturn();
        
        OrderResponse response = objectMapper.readValue(getResult.getResponse().getContentAsString(), OrderResponse.class);
        assertEquals(1, response.orderItems().size());
        assertEquals("Monster White", response.orderItems().get(0).productName());
        
        // Price check: 2.49 * 10 * 1.07 = 26.643
        // The system seems to round to 2 decimal places in some paths
        assertTrue(new BigDecimal("26.64").compareTo(response.totalPrice()) == 0);

        // 4. Apply Coupon (seeded SCANLY10)
        ApplyCouponRequest couponRequest = new ApplyCouponRequest("SCANLY10");
        mockMvc.perform(post("/api/orders/" + orderId + "/coupon")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(couponRequest)))
                .andExpect(status().isOk());

        // 5. Final check
        getResult = mockMvc.perform(get("/api/orders/" + orderId))
                .andExpect(status().isOk())
                .andReturn();
        
        response = objectMapper.readValue(getResult.getResponse().getContentAsString(), OrderResponse.class);
        // 10% discount on 26.643
        assertTrue(response.totalPrice().compareTo(new BigDecimal("26.6430")) < 0);
    }
}

