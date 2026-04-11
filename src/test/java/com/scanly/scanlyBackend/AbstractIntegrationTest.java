package com.scanly.scanlyBackend;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.junit.jupiter.api.BeforeEach;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

    protected MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    protected ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    static final PostgreSQLContainer<?> POSTGRES_CONTAINER = 
        new PostgreSQLContainer<>("postgres:17-alpine");

    static {
        POSTGRES_CONTAINER.start();
    }

    @BeforeEach
    void setupMockMvc() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop"); 
    }
}
