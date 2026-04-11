package com.scanly.scanlyBackend.services;

import com.scanly.scanlyBackend.dtos.AddProductRequest;
import com.scanly.scanlyBackend.dtos.ChangeProductRequest;
import com.scanly.scanlyBackend.dtos.ProductResponse;
import com.scanly.scanlyBackend.exceptions.ProductNotFoundException;
import com.scanly.scanlyBackend.models.Product;
import com.scanly.scanlyBackend.models.enums.PricingType;
import com.scanly.scanlyBackend.models.enums.ProductCategory;
import com.scanly.scanlyBackend.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetByCategory() {
        Product p = new Product("123", "Apple", new BigDecimal("1.00"), new BigDecimal("0.07"), PricingType.UNIT, ProductCategory.FRUITS_VEGETABLES);
        when(productRepository.findAllByProductCategory(ProductCategory.FRUITS_VEGETABLES)).thenReturn(List.of(p));

        List<ProductResponse> result = productService.getByCategory("FRUITS_VEGETABLES");

        assertEquals(1, result.size());
        assertEquals("Apple", result.get(0).name());
    }

    @Test
    void testGetByInvalidCategory() {
        assertThrows(ResponseStatusException.class, () -> productService.getByCategory("INVALID"));
    }

    @Test
    void testAddProduct() {
        AddProductRequest request = new AddProductRequest("123", "Apple", ProductCategory.FRUITS_VEGETABLES, new BigDecimal("1.00"), new BigDecimal("0.07"));
        
        productService.addProduct(request);
        
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testChangeProductNotFound() {
        ChangeProductRequest request = new ChangeProductRequest("123", "Apple", new BigDecimal("1.00"), new BigDecimal("0.07"), ProductCategory.FRUITS_VEGETABLES);
        when(productRepository.findByCode("123")).thenReturn(Optional.empty());
        
        assertThrows(ProductNotFoundException.class, () -> productService.changeProduct("123", request));
    }

    @Test
    void testDeleteProduct() {
        Product p = new Product();
        when(productRepository.findByCode("123")).thenReturn(Optional.of(p));
        
        productService.deleteProduct("123");
        
        verify(productRepository, times(1)).delete(p);
    }

    @Test
    void testGetAll() {
        Product p = new Product("123", "Apple", new BigDecimal("1.00"), new BigDecimal("0.07"), PricingType.UNIT, ProductCategory.FRUITS_VEGETABLES);
        when(productRepository.findAll()).thenReturn(List.of(p));

        List<ProductResponse> result = productService.getAll();

        assertEquals(1, result.size());
        assertEquals("Apple", result.get(0).name());
    }

    @Test
    void testFindByBarcode() {
        Product p = new Product("123", "Apple", new BigDecimal("1.00"), new BigDecimal("0.07"), PricingType.UNIT, ProductCategory.FRUITS_VEGETABLES);
        when(productRepository.findByCode("123")).thenReturn(Optional.of(p));

        ProductResponse result = productService.findByBarcode("123");

        assertEquals("Apple", result.name());
        assertEquals("123", result.code());
    }
}
