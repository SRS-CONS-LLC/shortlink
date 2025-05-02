# Use a lightweight JDK base image
FROM eclipse-temurin:21-jdk-alpine

# Set working directory
WORKDIR /app

# Copy Gradle wrapper and project files
COPY . .

# Build the Spring Boot application using Gradle
RUN ./gradlew build -x test

# Run the Spring Boot application
CMD ["java", "-jar", "build/libs/YOUR_APP_NAME.jar"]
