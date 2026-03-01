package com.scanly.scanlyBackend.repository;

import com.scanly.scanlyBackend.AbstractIntegrationTest;
import com.scanly.scanlyBackend.models.Product; 
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ProductRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void shouldSaveAndRetrieveProductByBarcode() {
        Product apfel = new Product();
        apfel.setName("Bio Apfel");
        apfel.setCode("123456789");
        apfel.setPricePerUnit(new BigDecimal(1.99));

        productRepository.save(apfel);
        Product foundProduct = productRepository.findByCode("123456789").orElse(null);

        assertThat(foundProduct).isNotNull();
        assertThat(foundProduct.getName()).isEqualTo("Bio Apfel");
    }
}