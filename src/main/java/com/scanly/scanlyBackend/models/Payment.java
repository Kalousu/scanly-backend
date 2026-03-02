package com.scanly.scanlyBackend.models;

import com.scanly.scanlyBackend.models.enums.Currency;
import com.scanly.scanlyBackend.models.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.scheduling.annotation.Async;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    @OneToOne
    private Order order;
    private BigDecimal amount;
    private String paymentMethod;
    @Enumerated(EnumType.STRING)
    private Currency currency;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    public Payment(Order order, BigDecimal amount, String paymentMethod, Currency currency, PaymentStatus status) {
        this.order = order;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.currency = currency;
        this.status = status;
    }
}
