package com.scanly.scanlyBackend;

import com.scanly.scanlyBackend.dtos.CouponRequest;
import com.scanly.scanlyBackend.dtos.CouponValidationResponse;
import com.scanly.scanlyBackend.models.enums.CouponType;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CouponFlowIntegrationTest extends AbstractIntegrationTest {

    @Test
    void testCouponLifecycle() throws Exception {
        // 1. Create Coupon
        CouponRequest request = new CouponRequest(
                "INTEGRATION_TEST",
                "Integration Test Coupon",
                CouponType.PERCENTAGE,
                new BigDecimal("20.00"),
                new BigDecimal("10.00"),
                true,
                null,
                null,
                100
        );

        mockMvc.perform(post("/api/coupons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // 2. Validate Coupon (Happy Path)
        MvcResult validateResult = mockMvc.perform(get("/api/coupons/validate/INTEGRATION_TEST?subtotal=50.00"))
                .andExpect(status().isOk())
                .andReturn();
        
        CouponValidationResponse valResponse = objectMapper.readValue(validateResult.getResponse().getContentAsString(), CouponValidationResponse.class);
        assertTrue(valResponse.valid());
        assertEquals(0, new BigDecimal("10.00").compareTo(valResponse.discount())); // 20% of 50.00

        // 3. Validate Coupon (Below Min Value)
        validateResult = mockMvc.perform(get("/api/coupons/validate/INTEGRATION_TEST?subtotal=5.00"))
                .andExpect(status().isBadRequest())
                .andReturn();
        
        valResponse = objectMapper.readValue(validateResult.getResponse().getContentAsString(), CouponValidationResponse.class);
        assertFalse(valResponse.valid());
        assertTrue(valResponse.message().contains("Mindestbestellwert"));
    }
}
