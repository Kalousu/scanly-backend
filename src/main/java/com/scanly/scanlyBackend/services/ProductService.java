package com.scanly.scanlyBackend.services;

import com.scanly.scanlyBackend.dtos.AddProductRequest;
import com.scanly.scanlyBackend.dtos.ChangeProductRequest;
import com.scanly.scanlyBackend.dtos.ProductResponse;
import com.scanly.scanlyBackend.exceptions.ProductNotFoundException;
import com.scanly.scanlyBackend.models.Product;
import com.scanly.scanlyBackend.models.enums.PricingType;
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
                        product.getPricePerUnit(),
                        product.getTaxRate()
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
                        product.getPricePerUnit(),
                        product.getTaxRate()
                        )
                ).toList();
    }

    public ProductResponse findByBarcode(String barcode){
        return productRepository.findByCode(barcode)
                .map(product -> new ProductResponse(
                        product.getCode(),
                        product.getName(),
                        product.getProductCategory(),
                        product.getPricePerUnit(),
                        product.getTaxRate()
                ))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produkt mit Barcode " + barcode + " nicht gefunden"));
    }

    public void addProduct(AddProductRequest request){
        Product product = new Product(
                request.code(),
                request.name(),
                request.price(),
                request.taxRate(),
                PricingType.WEIGHT,
                request.category()
        );
        productRepository.save(product);
    }

    public void changeProduct(String code, ChangeProductRequest request){
        Optional<Product> productOptional = productRepository.findByCode(code);
        if(productOptional.isPresent()){
            Product product = productOptional.get();
            product.setName(request.name());
            product.setCode(request.code());
            product.setPricePerUnit(request.price());
            product.setTaxRate(request.taxRate());
            product.setPricingType(PricingType.UNIT);
            product.setProductCategory(request.productCategory());
            productRepository.save(productOptional.get());
        } else {
            throw new ProductNotFoundException("Product with code " + code + " not found");
        }
    }

    public void deleteProduct(String code){
        Optional<Product> productOptional = productRepository.findByCode(code);
        if(productOptional.isPresent()){
            Product product = productOptional.get();
            productRepository.delete(product);
        }else {
            throw new ProductNotFoundException("Product with code " + code + " not found");
        }

    }
}
