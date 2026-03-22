package com.scanly.scanlyBackend.controllers;

import com.scanly.scanlyBackend.dtos.AddOrderItemRequest;
import com.scanly.scanlyBackend.dtos.OrderResponse;
import com.scanly.scanlyBackend.dtos.PaymentRequest;
import com.scanly.scanlyBackend.exceptions.ProductNotFoundException;
import com.scanly.scanlyBackend.models.Order;
import com.scanly.scanlyBackend.services.OrderService;
import com.scanly.scanlyBackend.services.PaymentService;
import com.scanly.scanlyBackend.services.ProductService;
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

    @Autowired
    private PaymentService paymentService;

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return new ResponseEntity<>(orderService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable long orderId) {
        return new ResponseEntity<>(orderService.getById(orderId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Long> createOrder() {
        return new ResponseEntity<>(orderService.createOrder(), HttpStatus.CREATED);
    }

    @PostMapping("/{orderId}/items")
    public ResponseEntity<Object> addItem(
            @PathVariable Long orderId,
            @RequestBody AddOrderItemRequest item
    ) {
        try{
            orderService.addItem(orderId, item);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch(ProductNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{orderId}/checkout")
    public ResponseEntity<Object> checkout(
            @PathVariable Long orderId,
            @RequestBody PaymentRequest paymentRequest
    ){
        try{
            paymentService.processPayment(orderId, paymentRequest);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch(Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
