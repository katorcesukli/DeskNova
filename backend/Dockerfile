# Use OpenJDK as base image
FROM eclipse-temurin:21

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the target directory
COPY build/libs/desk_nova-0.0.1-SNAPSHOT.jar app.jar

# Expose application port
EXPOSE 8080

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]