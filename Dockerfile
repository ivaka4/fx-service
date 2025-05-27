# Use official Eclipse Temurin JDK 17
FROM eclipse-temurin:17-jdk-jammy

# Create app directory
WORKDIR /app

# Copy built jar (assumes mvn package has run)
COPY target/fx-exchange-0.1.0.jar app.jar

# Expose port
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]
