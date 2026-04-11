# Build stage
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

# Copy the maven wrapper files explicitly
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Fix potential Windows line ending issues and ensure execution permissions
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw

# Resolve dependencies (this layer is cached if pom.xml doesn't change)
RUN ./mvnw dependency:go-offline -B

# Copy the source code and build the application
COPY src ./src
RUN ./mvnw clean package -DskipTests -B

# Run stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
