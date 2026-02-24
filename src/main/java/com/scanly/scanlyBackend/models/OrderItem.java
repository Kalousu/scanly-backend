package com.scanly.scanlyBackend.models;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class OrderItem {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Order order;
    @ManyToOne
    private Product product;

    private int quantity;
    private double price;
}
