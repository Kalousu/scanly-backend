package com.scanly.scanlyBackend.models;

import com.scanly.scanlyBackend.models.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    public Order(OrderStatus status){
        this.status = status;
    }

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long orderId;
    @OneToMany(mappedBy = "order")
    private List<OrderItem> items;
    @CreationTimestamp
    private Instant creationDate;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
}
