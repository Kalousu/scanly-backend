package com.scanly.scanlyBackend.controllers;

import java.util.List;

import com.scanly.scanlyBackend.dtos.AddProductRequest;
import com.scanly.scanlyBackend.dtos.ChangeProductRequest;
import com.scanly.scanlyBackend.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(@PathVariable String category) {
        return new ResponseEntity<>(productService.getByCategory(category), HttpStatus.OK);
    }

    @GetMapping("/barcode/{barcode}")
    public ResponseEntity<ProductResponse> getProductByBarcode(@PathVariable String barcode) {
        return new ResponseEntity<>(productService.findByBarcode(barcode), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Object> addProduct(
            @RequestBody AddProductRequest addProductRequest
    ){
        productService.addProduct(addProductRequest);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @PatchMapping("/barcode/{barcode}")
    public ResponseEntity<Object> updateProduct(
            @PathVariable String barcode,
            @RequestBody ChangeProductRequest changeProductRequest
    ){
        productService.changeProduct(barcode, changeProductRequest);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/barcode/{barcode}")
    public ResponseEntity<Object> deleteProduct(
            @PathVariable String barcode
    ){
        productService.deleteProduct(barcode);
        return new ResponseEntity(HttpStatus.OK);
    }
}
