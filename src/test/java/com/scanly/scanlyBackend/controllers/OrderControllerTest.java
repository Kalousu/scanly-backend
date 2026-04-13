package com.scanly.scanlyBackend.controllers;

import com.scanly.scanlyBackend.dtos.*;
import com.scanly.scanlyBackend.models.enums.OrderStatus;
import com.scanly.scanlyBackend.services.OrderService;
import com.scanly.scanlyBackend.services.PaymentService;
import com.scanly.scanlyBackend.services.ReceiptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private ReceiptService receiptService;

    @InjectMocks
    private OrderController orderController;

    private OrderResponse testOrderResponse;
    private ReceiptResponse testReceiptResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        Instant now = Instant.now();
        
        testOrderResponse = new OrderResponse(
            1L,
            now,
            List.of(),
            new BigDecimal("100.00"),
            OrderStatus.OPEN
        );

        testReceiptResponse = new ReceiptResponse(
            1L,
            now,
            List.of(),
            new BigDecimal("100.00"),
            List.of()
        );
    }

    @Test
    void getAllOrders_ShouldReturnListOfOrders() {
        List<OrderResponse> orders = Arrays.asList(testOrderResponse);
        when(orderService.getAll()).thenReturn(orders);

        ResponseEntity<List<OrderResponse>> response = orderController.getAllOrders();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(orderService, times(1)).getAll();
    }

    @Test
    void getOrderById_ShouldReturnOrder() {
        when(orderService.getById(1L)).thenReturn(testOrderResponse);

        ResponseEntity<OrderResponse> response = orderController.getOrderById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().orderId());
        verify(orderService, times(1)).getById(1L);
    }

    @Test
    void getOrderReceiptById_ShouldReturnReceipt() {
        when(receiptService.getOrderReceiptById(1L)).thenReturn(testReceiptResponse);

        ResponseEntity<ReceiptResponse> response = orderController.getOrderReceiptById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().orderId());
        verify(receiptService, times(1)).getOrderReceiptById(1L);
    }

    @Test
    void deleteOrderById_ShouldReturnOk() {
        doNothing().when(orderService).deleteOrder(1L);

        ResponseEntity<Object> response = orderController.deleteOrderById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(orderService, times(1)).deleteOrder(1L);
    }

    @Test
    void createOrder_ShouldReturnOrderId() {
        when(orderService.createOrder()).thenReturn(1L);

        ResponseEntity<Long> response = orderController.createOrder();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody());
        verify(orderService, times(1)).createOrder();
    }

    @Test
    void addItem_ShouldReturnOk() {
        AddOrderItemRequest request = new AddOrderItemRequest("123456", new BigDecimal("2"));
        doNothing().when(orderService).addItem(eq(1L), any(AddOrderItemRequest.class));

        ResponseEntity<Object> response = orderController.addItem(1L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(orderService, times(1)).addItem(eq(1L), any(AddOrderItemRequest.class));
    }

    @Test
    void changeItem_ShouldReturnOk() {
        UpdateItemQuantityRequest request = new UpdateItemQuantityRequest(new BigDecimal("3"));
        doNothing().when(orderService).updateItemQuantity(eq(1L), eq(1L), any(UpdateItemQuantityRequest.class));

        ResponseEntity<Object> response = orderController.changeItem(1L, 1L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(orderService, times(1)).updateItemQuantity(eq(1L), eq(1L), any(UpdateItemQuantityRequest.class));
    }

    @Test
    void deleteItem_ShouldReturnOk() {
        doNothing().when(orderService).deleteItem(1L, 1L);

        ResponseEntity<Object> response = orderController.deleteItem(1L, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(orderService, times(1)).deleteItem(1L, 1L);
    }

    @Test
    void checkout_ShouldReturnAccepted() {
        PaymentRequest paymentRequest = new PaymentRequest("CASH");
        doNothing().when(paymentService).processPayment(eq(1L), any(PaymentRequest.class));

        ResponseEntity<Object> response = orderController.checkout(1L, paymentRequest);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        verify(paymentService, times(1)).processPayment(eq(1L), any(PaymentRequest.class));
    }

    @Test
    void applyCoupon_ShouldReturnOk() {
        ApplyCouponRequest request = new ApplyCouponRequest("TEST10");
        doNothing().when(orderService).applyCoupon(1L, "TEST10");

        ResponseEntity<Object> response = orderController.applyCoupon(1L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(orderService, times(1)).applyCoupon(1L, "TEST10");
    }

    @Test
    void removeCoupon_ShouldReturnOk() {
        doNothing().when(orderService).removeCoupon(1L);

        ResponseEntity<Object> response = orderController.removeCoupon(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(orderService, times(1)).removeCoupon(1L);
    }
}
