package com.scanly.scanlyBackend.services;

import com.scanly.scanlyBackend.models.Order;
import com.scanly.scanlyBackend.models.enums.OrderStatus;
import com.scanly.scanlyBackend.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    @Autowired
    OrderRepository orderRepo;

    public List<Order> getAll(){
        return orderRepo.findAll();
    }

    public Long createOrder(){
        Order savedOrder = orderRepo.save(new Order(OrderStatus.OPEN));
        return savedOrder.getOrderId();
    }
}
