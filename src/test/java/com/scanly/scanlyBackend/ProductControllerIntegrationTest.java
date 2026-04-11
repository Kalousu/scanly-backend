package com.scanly.scanlyBackend;

import com.scanly.scanlyBackend.dtos.ProductResponse;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductControllerIntegrationTest extends AbstractIntegrationTest {

    @Test
    void testGetProductByBarcode() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/products/barcode/1234567890123"))
                .andExpect(status().isOk())
                .andReturn();
        
        ProductResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), ProductResponse.class);
        assertEquals("Monster White", response.name());
    }

    @Test
    void testGetProductByBarcodeNotFound() throws Exception {
        mockMvc.perform(get("/api/products/barcode/0000000000000"))
                .andExpect(status().isNotFound());
    }
}
