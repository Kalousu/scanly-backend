package com.scanly.scanlyBackend.controllers;

import com.scanly.scanlyBackend.dtos.PaymentResponse;
import com.scanly.scanlyBackend.models.Payment;
import com.scanly.scanlyBackend.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getAllPayments(){
        return new ResponseEntity<>(paymentService.getAll(), HttpStatus.OK);
    }
}
