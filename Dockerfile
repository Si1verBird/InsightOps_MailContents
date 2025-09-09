# Multi-stage build for Spring Boot application
FROM eclipse-temurin:17-jdk AS builder

# Set working directory
WORKDIR /app

# Copy gradle files first (for better caching)
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Make gradlew executable
RUN chmod +x ./gradlew

# Download dependencies (for better caching)
RUN ./gradlew dependencies --no-daemon || true

# Copy source code
COPY src src

# Build the application with more memory
RUN ./gradlew build -x test --no-daemon -Dorg.gradle.jvmargs="-Xmx2048m"

# Runtime stage
FROM eclipse-temurin:17-jre

# Set working directory
WORKDIR /app

# Copy the built jar from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose port (Azure will override with PORT env var)
EXPOSE 8080

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=docker

# Run the application with Azure-compatible port binding
ENTRYPOINT ["java", "-Dserver.port=${PORT:-8080}", "-jar", "app.jar"]
