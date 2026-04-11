package com.scanly.scanlyBackend.controllers;

import com.scanly.scanlyBackend.dtos.AddOrderItemRequest;
import com.scanly.scanlyBackend.dtos.ApplyCouponRequest;
import com.scanly.scanlyBackend.dtos.OrderResponse;
import com.scanly.scanlyBackend.dtos.PaymentRequest;
import com.scanly.scanlyBackend.dtos.UpdateItemQuantityRequest;
import com.scanly.scanlyBackend.dtos.receipts.ReceiptResponse;
import com.scanly.scanlyBackend.exceptions.CouponNotFoundException;
import com.scanly.scanlyBackend.exceptions.InvalidCouponException;
import com.scanly.scanlyBackend.exceptions.ProductNotFoundException;
import com.scanly.scanlyBackend.models.Order;
import com.scanly.scanlyBackend.services.OrderService;
import com.scanly.scanlyBackend.services.PaymentService;
import com.scanly.scanlyBackend.services.ProductService;
import com.scanly.scanlyBackend.services.ReceiptService;
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

    @Autowired
    private ReceiptService receiptService;

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return new ResponseEntity<>(orderService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable long orderId) {
        return new ResponseEntity<>(orderService.getById(orderId), HttpStatus.OK);
    }

    @GetMapping("/{orderId}/receipt")
    public ResponseEntity<ReceiptResponse> getOrderReceiptById(@PathVariable long orderId) {
        return new ResponseEntity<ReceiptResponse>(receiptService.getOrderReceiptById(orderId), HttpStatus.OK);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Object> deleteOrderById(@PathVariable long orderId) {
        orderService.deleteOrder(orderId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Long> createOrder() {
        return new ResponseEntity<>(orderService.createOrder(), HttpStatus.CREATED);
    }

    @PostMapping("/{orderId}/items")
    public ResponseEntity<Object> addItem(
            @PathVariable Long orderId,
            @RequestBody AddOrderItemRequest item
    ){
            orderService.addItem(orderId, item);
            return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/{orderId}/items/{itemId}")
    public ResponseEntity<Object> changeItem(
            @PathVariable Long orderId,
            @PathVariable Long itemId,
            @RequestBody UpdateItemQuantityRequest request
    ){
        orderService.updateItemQuantity(orderId, itemId, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{orderId}/items/{itemId}")
    public ResponseEntity<Object> deleteItem(
            @PathVariable Long orderId,
            @PathVariable Long itemId
    ){
        orderService.deleteItem(orderId, itemId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/{orderId}/checkout")
    public ResponseEntity<Object> checkout(
            @PathVariable Long orderId,
            @RequestBody PaymentRequest paymentRequest
    ){
            paymentService.processPayment(orderId, paymentRequest);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PostMapping("/{orderId}/coupon")
    public ResponseEntity<Object> applyCoupon(
            @PathVariable Long orderId,
            @RequestBody ApplyCouponRequest request
    ){
            orderService.applyCoupon(orderId, request.code());
            return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{orderId}/coupon")
    public ResponseEntity<Object> removeCoupon(@PathVariable Long orderId){
        orderService.removeCoupon(orderId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
