# Use an official OpenJDK runtime as a parent image
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the built JAR file from the target directory into the container
COPY . .

# Expose the application's port (update if your app uses a different port)
EXPOSE 8080

# Command to run the application
CMD ["java", "-jar", "app.jar"]
