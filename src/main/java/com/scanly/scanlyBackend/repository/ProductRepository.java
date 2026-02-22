package com.scanly.scanlyBackend.repository;

import com.scanly.scanlyBackend.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;



public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByBarcode(String barcode);
}
