# Use a lightweight JDK base image
FROM eclipse-temurin:21-jdk-alpine

# Set working directory
WORKDIR /app

# Copy everything
COPY . .

# Give execution permission to gradlew
RUN chmod +x ./gradlew

# Build the Spring Boot app without running tests
RUN ./gradlew build -x test

# Run the Spring Boot jar
CMD ["sh", "-c", "java -jar build/libs/*.jar"]
