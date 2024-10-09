# Use an official OpenJDK runtime as a parent image
FROM openjdk:21-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the pre-built jar
COPY build/libs/examprep_backend-0.0.1-SNAPSHOT.jar /app/examprep_backend.jar

# Expose the port that the app will run on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "examprep_backend.jar"]
