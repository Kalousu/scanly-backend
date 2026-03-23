package com.scanly.scanlyBackend.services;

import com.scanly.scanlyBackend.dtos.ProductResponse;
import com.scanly.scanlyBackend.models.Product;
import com.scanly.scanlyBackend.models.enums.ProductCategory;
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
                        product.getProductCategory(),
                        product.getPricePerUnit()
                ))
                .toList();
    }

    public List<ProductResponse> getByCategory(String category){
        ProductCategory productCategory;

        try {
            productCategory = ProductCategory.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Ungültige Kategorie: " + category);
        }

        return productRepository.findAllByProductCategory(productCategory).stream()
                .map(product -> new ProductResponse(
                        product.getCode(),
                        product.getName(),
                        product.getProductCategory(),
                        product.getPricePerUnit()
                        )
                ).toList();
    }

    public ProductResponse findByBarcode(String barcode){
        return productRepository.findByCode(barcode)
                .map(product -> new ProductResponse(
                        product.getCode(),
                        product.getName(),
                        product.getProductCategory(),
                        product.getPricePerUnit()
                ))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produkt mit Barcode " + barcode + " nicht gefunden"));
    }
}
