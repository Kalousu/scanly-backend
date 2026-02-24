package com.scanly.scanlyBackend.repository;

import com.scanly.scanlyBackend.AbstractIntegrationTest;
import com.scanly.scanlyBackend.models.Product; 
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.Assertions.assertThat;

class ProductRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void shouldSaveAndRetrieveProductByBarcode() {
        Product apfel = new Product();
        apfel.setName("Bio Apfel");
        apfel.setBarcode("123456789");
        apfel.setPrice(1.99);

        productRepository.save(apfel);
        Product foundProduct = productRepository.findByBarcode("123456789").orElse(null);

        assertThat(foundProduct).isNotNull();
        assertThat(foundProduct.getName()).isEqualTo("Bio Apfel");
    }
}