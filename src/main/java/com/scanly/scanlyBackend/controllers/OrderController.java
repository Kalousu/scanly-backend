package com.scanly.scanlyBackend.controllers;

import com.scanly.scanlyBackend.models.Order;
import com.scanly.scanlyBackend.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return new ResponseEntity<>(orderService.getAll(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Long> createOrder() {
        return new ResponseEntity<>(orderService.createOrder(), HttpStatus.CREATED);
    }
}
