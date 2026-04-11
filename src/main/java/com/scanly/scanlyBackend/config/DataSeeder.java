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
                productRepository.save(new Product("5555555555555", "Schoko Crossong", BigDecimal.valueOf(0.79), BigDecimal.valueOf(0.19), PricingType.UNIT, ProductCategory.OTHERS));
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
