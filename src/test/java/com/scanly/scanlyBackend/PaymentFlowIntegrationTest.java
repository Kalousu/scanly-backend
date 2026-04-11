package com.scanly.scanlyBackend;

import com.scanly.scanlyBackend.dtos.OrderResponse;
import com.scanly.scanlyBackend.dtos.PaymentRequest;
import com.scanly.scanlyBackend.models.enums.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

class PaymentFlowIntegrationTest extends AbstractIntegrationTest {

    @Test
    void testCheckoutAndPaymentAsync() throws Exception {
        // 1. Create Order
        MvcResult createResult = mockMvc.perform(post("/api/orders"))
                .andExpect(status().isCreated())
                .andReturn();
        Long orderId = Long.parseLong(createResult.getResponse().getContentAsString());

        // 2. Checkout
        PaymentRequest paymentRequest = new PaymentRequest("CARD");
        mockMvc.perform(post("/api/orders/" + orderId + "/checkout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isAccepted());

        // 3. Await Async Processing (PaymentService has Thread.sleep(2000))
        await().atMost(5, TimeUnit.SECONDS).until(() -> {
            MvcResult getResult = mockMvc.perform(get("/api/orders/" + orderId))
                    .andReturn();
            OrderResponse response = objectMapper.readValue(getResult.getResponse().getContentAsString(), OrderResponse.class);
            return response.orderStatus() == OrderStatus.CLOSED;
        });

        // 4. Final verify
        MvcResult finalGet = mockMvc.perform(get("/api/orders/" + orderId))
                .andExpect(status().isOk())
                .andReturn();
        OrderResponse finalResponse = objectMapper.readValue(finalGet.getResponse().getContentAsString(), OrderResponse.class);
        assertEquals(OrderStatus.CLOSED, finalResponse.orderStatus());
    }
}
