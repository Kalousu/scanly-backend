package com.scanly.scanlyBackend.services;

import com.scanly.scanlyBackend.dtos.receipts.ReceiptResponse;
import com.scanly.scanlyBackend.exceptions.OrderNotFoundException;
import com.scanly.scanlyBackend.mappers.ReceiptMapper;
import com.scanly.scanlyBackend.models.Order;
import com.scanly.scanlyBackend.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReceiptService {
    @Autowired
    OrderRepository orderRepository;

    public ReceiptResponse getOrderReceiptById(Long orderId){
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order with id " + orderId + " not found"));
        ReceiptMapper receiptMapper = new ReceiptMapper();
        return receiptMapper.toReceiptResponse(order);
    }
}
