package com.scanly.scanlyBackend.repository;

import com.scanly.scanlyBackend.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
