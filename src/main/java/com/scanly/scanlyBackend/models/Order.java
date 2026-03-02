package com.scanly.scanlyBackend.models;

import com.scanly.scanlyBackend.models.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {

    public Order(OrderStatus status){
        this.status = status;
    }

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long orderId;
    private BigDecimal totalPrice;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items;
    @CreationTimestamp
    private Instant creationDate;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    public void addItem(OrderItem item){
        items.add(item);
    }
}
