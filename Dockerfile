FROM openjdk:17-jdk-alpine

# Create app directory
WORKDIR /app

# Copy built jar (assumes mvn package has run)
COPY target/fx-exchange-0.1.0.jar app.jar

# Expose port
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]
