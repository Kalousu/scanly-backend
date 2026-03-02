package com.scanly.scanlyBackend.services;

import com.scanly.scanlyBackend.dtos.ProductResponse;
import com.scanly.scanlyBackend.models.Product;
import com.scanly.scanlyBackend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    ProductRepository productRepository;

    public List<ProductResponse> getAll(){
        return productRepository.findAll().stream()
                .map(product -> new ProductResponse(
                        product.getCode(),
                        product.getName(),
                        product.getPricePerUnit()
                ))
                .toList();
    }

    public ProductResponse findByBarcode(String barcode){
        return productRepository.findByCode(barcode)
                .map(product -> new ProductResponse(
                        product.getCode(),
                        product.getName(),
                        product.getPricePerUnit()
                ))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produkt mit Barcode " + barcode + " nicht gefunden"));
    }

    public Optional<Product> findEntityByBarcode(String barcode){
        return productRepository.findByCode(barcode);
    }
}
