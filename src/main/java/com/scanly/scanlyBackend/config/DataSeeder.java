package com.scanly.scanlyBackend.config;

import com.scanly.scanlyBackend.models.Product;
import com.scanly.scanlyBackend.repository.ProductRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {
    
    @Bean
    CommandLineRunner initDatabase(ProductRepository productRepository) {
        return args -> {
            // Add sample products to the database, if empty
            if(productRepository.count() == 0) {
                productRepository.save(new Product(null, "1234567890123", "Monster White", 2.49));
                productRepository.save(new Product(null, "9876543210987", "Monster not White", 1.99));
                productRepository.save(new Product(null, "5555555555555", "Schoko Crossong", 0.79));
            }
        };
    }
}
