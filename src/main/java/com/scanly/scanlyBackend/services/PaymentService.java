package com.scanly.scanlyBackend.services;

import com.scanly.scanlyBackend.dtos.PaymentRequest;
import com.scanly.scanlyBackend.dtos.PaymentResponse;
import com.scanly.scanlyBackend.dtos.ProductResponse;
import com.scanly.scanlyBackend.exceptions.OrderNotFoundException;
import com.scanly.scanlyBackend.models.Order;
import com.scanly.scanlyBackend.models.Payment;
import com.scanly.scanlyBackend.models.enums.Currency;
import com.scanly.scanlyBackend.models.enums.OrderStatus;
import com.scanly.scanlyBackend.models.enums.PaymentStatus;
import com.scanly.scanlyBackend.repository.OrderRepository;
import com.scanly.scanlyBackend.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private OrderRepository orderRepository;

    public List<PaymentResponse> getAll(){
        return paymentRepository.findAll().stream()
                .map(payment -> new PaymentResponse(
                        payment.getId(),
                        payment.getOrder().getOrderId(),
                        payment.getOrder().getTotalPrice(),
                        payment.getPaymentMethod(),
                        payment.getCurrency(),
                        payment.getStatus()
                ))
                .toList();
    }

    @Transactional
    @Async
    public void processPayment(Long orderId, PaymentRequest paymentRequest) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order with ID " + orderId + " not found"));
        Payment payment = new Payment(order, order.getTotalPrice(), paymentRequest.paymentMethod(), Currency.EURO, PaymentStatus.OPEN);
        try{
            Thread.sleep(2000 + new Random().nextInt(2000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        payment.setStatus(PaymentStatus.PAID);
        order.setStatus(OrderStatus.CLOSED);
        paymentRepository.save(payment);
        orderRepository.save(order);
    }
}
