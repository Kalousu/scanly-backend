package com.scanly.scanlyBackend.config;

import com.scanly.scanlyBackend.models.Order;
import com.scanly.scanlyBackend.models.Product;
import com.scanly.scanlyBackend.models.enums.OrderStatus;
import com.scanly.scanlyBackend.models.enums.PricingType;
import com.scanly.scanlyBackend.repository.OrderRepository;
import com.scanly.scanlyBackend.repository.ProductRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;


@Configuration
public class DataSeeder {
    
    @Bean
    CommandLineRunner initDatabase(ProductRepository productRepository, OrderRepository orderRepository) {
        return args -> {
            // Add sample products to the database, if empty
            if(productRepository.count() == 0) {
                productRepository.save(new Product(null, "1234567890123", "Monster White", BigDecimal.valueOf(2.49), PricingType.UNIT));
                productRepository.save(new Product(null, "9876543210987", "Monster not White", BigDecimal.valueOf(99), PricingType.UNIT));
                productRepository.save(new Product(null, "5555555555555", "Schoko Crossong", BigDecimal.valueOf(0.79), PricingType.UNIT));
            }

            if(orderRepository.count() == 0){
                Order order = new Order();
                order.setStatus(OrderStatus.OPEN);
                orderRepository.save(order);
            }
        };
    }
}
