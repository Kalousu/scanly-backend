package com.scanly.scanlyBackend.models;

import com.scanly.scanlyBackend.models.enums.PricingType;
import com.scanly.scanlyBackend.models.enums.ProductCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal pricePerUnit;

    @Column(nullable = false)
    private BigDecimal taxRate;

    @Enumerated(EnumType.STRING)
    private PricingType pricingType;

    @Enumerated(EnumType.STRING)
    private ProductCategory productCategory;

    public Product(String code, String name, BigDecimal pricePerUnit, BigDecimal taxRate, PricingType pricingType, ProductCategory productCategory) {
        this.code = code;
        this.name = name;
        this.pricePerUnit = pricePerUnit;
        this.taxRate = taxRate;
        this.pricingType = pricingType;
        this.productCategory = productCategory;
    }
}
