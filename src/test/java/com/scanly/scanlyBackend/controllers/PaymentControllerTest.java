package com.scanly.scanlyBackend.controllers;

import com.scanly.scanlyBackend.dtos.PaymentResponse;
import com.scanly.scanlyBackend.models.enums.Currency;
import com.scanly.scanlyBackend.models.enums.PaymentStatus;
import com.scanly.scanlyBackend.services.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    private PaymentResponse testPaymentResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        testPaymentResponse = new PaymentResponse(
            1L,
            1L,
            new BigDecimal("100.00"),
            "CASH",
            Currency.EURO,
            PaymentStatus.PAID
        );
    }

    @Test
    void getAllPayments_ShouldReturnListOfPayments() {
        List<PaymentResponse> payments = Arrays.asList(testPaymentResponse);
        when(paymentService.getAll()).thenReturn(payments);

        ResponseEntity<List<PaymentResponse>> response = paymentController.getAllPayments();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).paymentId());
        verify(paymentService, times(1)).getAll();
    }

    @Test
    void getAllPayments_WhenEmpty_ShouldReturnEmptyList() {
        when(paymentService.getAll()).thenReturn(List.of());

        ResponseEntity<List<PaymentResponse>> response = paymentController.getAllPayments();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(paymentService, times(1)).getAll();
    }
}
