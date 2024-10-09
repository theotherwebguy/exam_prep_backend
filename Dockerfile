# Use an official OpenJDK runtime as a parent image
FROM openjdk:21-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the Gradle build files and source code
COPY build.gradle settings.gradle /app/
COPY gradlew /app/
COPY gradle/wrapper/ /app/gradle/wrapper/
COPY src /app/src

# Ensure gradlew is executable
RUN chmod +x ./gradlew

# Build the Spring Boot application
RUN ./gradlew clean build -x test --no-daemon

# Expose the port on which the Spring Boot app will run
EXPOSE 8080

# Define the entry point for the container to run the JAR file
CMD ["java", "-jar", "build/libs/examprep_backend-0.0.1-SNAPSHOT.jar"]
