package com.scanly.scanlyBackend.services;

import com.scanly.scanlyBackend.dtos.ReceiptResponse;
import com.scanly.scanlyBackend.exceptions.OrderNotFoundException;
import com.scanly.scanlyBackend.models.Order;
import com.scanly.scanlyBackend.models.enums.OrderStatus;
import com.scanly.scanlyBackend.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class ReceiptServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private ReceiptService receiptService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetOrderReceiptById() {
        Order order = new Order(OrderStatus.CLOSED);
        order.setOrderId(1L);
        order.setItems(new ArrayList<>());
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        ReceiptResponse response = receiptService.getOrderReceiptById(1L);

        assertNotNull(response);
    }

    @Test
    void testGetOrderReceiptByIdNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(OrderNotFoundException.class, () -> receiptService.getOrderReceiptById(1L));
    }
}
