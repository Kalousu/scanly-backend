# Scanly Backend

Core service for the Scanly self-checkout system. This application provides the REST API for product management, order processing, and payment integration.

## Technical Stack
- **Framework:** Spring Boot (Java)
- **Runtime:** Java 20/21
- **Database:** PostgreSQL
- **Containerization:** Docker / Docker Compose
- **Testing:** JUnit 5, Testcontainers

## Getting Started

### Prerequisites
- Docker and Docker Compose
- Java 21 JDK (for local development)
- Maven 3.9+ (or use the provided `./mvnw` wrapper)

### Running with Docker (Recommended)
The entire environment, including the database and the backend service, can be started with a single command:

```bash
docker-compose up --build
```

- **API Base URL:** `http://localhost:8080`
- **Database Port:** `5432`

### Local Development
To run the application locally while using a Dockerized database:

1. Start the database service:
   ```bash
   docker-compose up db -d
   ```

2. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

### Running Tests
The project utilizes Testcontainers for integration testing. Ensure Docker is running before executing tests.

```bash
./mvnw test
```

## Configuration

Configuration is managed via environment variables. Default values are provided for local development in `src/main/resources/application.properties`.

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_DATASOURCE_URL` | JDBC connection string | `jdbc:postgresql://localhost:5432/scanly_db` |
| `SPRING_DATASOURCE_USERNAME` | Database user | `scanly_user` |
| `SPRING_DATASOURCE_PASSWORD` | Database password | `scanly123` |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | Hibernate DDL strategy | `create-drop` |

## API Endpoints

The backend exposes several functional modules:

- **Product Catalog:** `/api/products` - Manage and retrieve product data.
- **Order Management:** `/api/orders` - Core checkout logic and cart state.
- **Coupon System:** `/api/coupons` - Validation and application of discounts.
- **Payment Processing:** `/api/payments` - Transaction state management.

For detailed testing of the endpoints, use the **Bruno** collections located in the `/scanly-bruno` directory.

## Project Structure
- `src/main/java/com/scanly/scanlyBackend/controllers`: REST API layer.
- `src/main/java/com/scanly/scanlyBackend/services`: Business logic layer.
- `src/main/java/com/scanly/scanlyBackend/repository`: Data access layer (JPA).
- `src/main/java/com/scanly/scanlyBackend/dtos`: Data Transfer Objects for API contracts.
- `src/main/java/com/scanly/scanlyBackend/models`: JPA Entity definitions.
