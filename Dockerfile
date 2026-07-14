# Stage 1: Builder
FROM eclipse-temurin:25-jdk-noble AS builder

WORKDIR /app

# Copy project files
COPY . .

# Fix Windows line endings in gradlew and all shell scripts
RUN find . -type f -name "*.sh" -exec sed -i 's/\r$//' {} +
RUN sed -i 's/\r$//' ./gradlew

# Fix Windows line endings in Java files to prevent spotless issues
RUN find . -type f -name "*.java" -exec sed -i 's/\r$//' {} +

# Make gradlew executable
RUN chmod +x ./gradlew

# Build application without running tests (spotless checks run normally)
RUN ./gradlew build -x test --no-daemon

# Stage 2: Runtime
FROM eclipse-temurin:25-jre-noble

WORKDIR /app

# Create non-root user using Ubuntu syntax
RUN groupadd --system spring && useradd --system --gid spring spring

# Copy jar from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Set permissions
RUN chown -R spring:spring /app
USER spring

# Expose port
EXPOSE 8080

# Entry point
ENTRYPOINT ["java", "-jar", "app.jar"]