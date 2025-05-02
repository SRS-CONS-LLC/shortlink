# Use the Eclipse Temurin JDK base image
FROM eclipse-temurin:21-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy all project files into the container
COPY . ./

# Ensure Gradle wrapper is executable
RUN chmod +x ./gradlew

# Build the application using Gradle (skipping tests)
RUN ./gradlew clean build -x test

# Run the app by dynamically finding the JAR in the build/libs directory
CMD ["sh", "-c", "java -jar build/libs/*.jar"]
