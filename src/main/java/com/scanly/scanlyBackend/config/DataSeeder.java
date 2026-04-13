package com.scanly.scanlyBackend.config;

import com.scanly.scanlyBackend.models.Coupon;
import com.scanly.scanlyBackend.models.Order;
import com.scanly.scanlyBackend.models.Product;
import com.scanly.scanlyBackend.models.enums.CouponType;
import com.scanly.scanlyBackend.models.enums.OrderStatus;
import com.scanly.scanlyBackend.models.enums.PricingType;
import com.scanly.scanlyBackend.models.enums.ProductCategory;
import com.scanly.scanlyBackend.repository.CouponRepository;
import com.scanly.scanlyBackend.repository.OrderRepository;
import com.scanly.scanlyBackend.repository.ProductRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;


@Configuration
public class DataSeeder {
    
    @Bean
    CommandLineRunner initDatabase(ProductRepository productRepository, OrderRepository orderRepository, CouponRepository couponRepository) {
        return args -> {
            if(productRepository.count() == 0) {
                productRepository.save(new Product("1234567890123", "Monster White", BigDecimal.valueOf(2.49), BigDecimal.valueOf(0.07), PricingType.UNIT, ProductCategory.OTHERS));
                productRepository.save(new Product("9876543210987", "Monster not White", BigDecimal.valueOf(99), BigDecimal.valueOf(0.19), PricingType.UNIT, ProductCategory.OTHERS));
                productRepository.save(new Product("1000", "Schoko Crossong", BigDecimal.valueOf(1.00), BigDecimal.valueOf(0.19), PricingType.UNIT, ProductCategory.BAKERY));
                productRepository.save(new Product("2000", "Bronane", BigDecimal.valueOf(1.10), BigDecimal.valueOf(0.19), PricingType.UNIT, ProductCategory.FRUITS_VEGETABLES));
                productRepository.save(new Product("3000", "Lauch", BigDecimal.valueOf(0.50), BigDecimal.valueOf(0.19), PricingType.UNIT, ProductCategory.FRUITS_VEGETABLES));
                productRepository.save(new Product("42143949", "Saskia Medium", BigDecimal.valueOf(0.50), BigDecimal.valueOf(0.19), PricingType.UNIT, ProductCategory.OTHERS));
                productRepository.save(new Product("10293847", "Evian Naturell 0,5L", BigDecimal.valueOf(0.79), BigDecimal.valueOf(0.19), PricingType.UNIT, ProductCategory.OTHERS));
                productRepository.save(new Product("20394857", "Adelholzener Classic 0,75L", BigDecimal.valueOf(1.09), BigDecimal.valueOf(0.19), PricingType.UNIT, ProductCategory.OTHERS));
                productRepository.save(new Product("30485967", "Coca-Cola 0,33L", BigDecimal.valueOf(1.29), BigDecimal.valueOf(0.19), PricingType.UNIT, ProductCategory.OTHERS));
                productRepository.save(new Product("40596071", "Bionade Holunder 0,33L", BigDecimal.valueOf(1.49), BigDecimal.valueOf(0.19), PricingType.UNIT, ProductCategory.OTHERS));
                productRepository.save(new Product("50607182", "Apfelsaft Naturtrüb 1L", BigDecimal.valueOf(1.89), BigDecimal.valueOf(0.07), PricingType.UNIT, ProductCategory.OTHERS));
                productRepository.save(new Product("60718293", "Orangensaft 1L", BigDecimal.valueOf(1.99), BigDecimal.valueOf(0.07), PricingType.UNIT, ProductCategory.OTHERS));

            }

            if(orderRepository.count() == 0){
                Order order = new Order();
                order.setStatus(OrderStatus.OPEN);
                orderRepository.save(order);
            }

            if(couponRepository.count() == 0) {
                Coupon c1 = new Coupon(
                    "SCANLY10",
                    "10% Rabatt",
                    CouponType.PERCENTAGE,
                    BigDecimal.valueOf(10),
                    BigDecimal.ZERO
                );
                c1.setActive(true);
                couponRepository.save(c1);
                
                Coupon c2 = new Coupon(
                    "SAVE5",
                    "5 EUR Rabatt ab 20 EUR",
                    CouponType.FIXED,
                    BigDecimal.valueOf(5),
                    BigDecimal.valueOf(20)
                );
                c2.setActive(true);
                couponRepository.save(c2);
                
                Coupon c3 = new Coupon(
                    "WELCOME15",
                    "15% Rabatt ab 30 EUR",
                    CouponType.PERCENTAGE,
                    BigDecimal.valueOf(15),
                    BigDecimal.valueOf(30)
                );
                c3.setActive(true);
                couponRepository.save(c3);
            }
        };
    }
}
