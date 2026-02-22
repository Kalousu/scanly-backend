package com.scanly.scanlyBackend.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.scanly.scanlyBackend.repository.ProductRepository;
import com.scanly.scanlyBackend.dtos.ProductResponse;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository productRepository;
    
    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(product -> new ProductResponse(
                        product.getBarcode(),
                        product.getName(),
                        product.getPrice()
                ))
                .toList();
    }

    @GetMapping("/{barcode}")
    public ProductResponse getProductByBarcode(@PathVariable String barcode) {
        return productRepository.findByBarcode(barcode)
                .map(product -> new ProductResponse(
                        product.getBarcode(),
                        product.getName(),
                        product.getPrice()
                ))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produkt mit Barcode " + barcode + " nicht gefunden"));
    }
}
