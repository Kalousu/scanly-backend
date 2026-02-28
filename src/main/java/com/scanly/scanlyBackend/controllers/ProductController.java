package com.scanly.scanlyBackend.controllers;

import java.util.List;

import com.scanly.scanlyBackend.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @Autowired
    ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return new ResponseEntity<>(productService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/{barcode}")
    public ResponseEntity<ProductResponse> getProductByBarcode(@PathVariable String barcode) {
        return new ResponseEntity<>(productService.findByBarcode(barcode), HttpStatus.OK);
    }
}
