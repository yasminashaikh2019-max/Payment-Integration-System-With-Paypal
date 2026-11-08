# Base image: Amazon Linux 2023
FROM amazonlinux:2023

# Set the working directory inside the container
WORKDIR /app

# Update packages, install Java 17, and clean package cache
RUN dnf update -y \
    && dnf install -y java-17-amazon-corretto-headless \
    && dnf clean all \
    && rm -rf /var/cache/dnf

# Copy the Spring Boot JAR from the host into /app
# Make sure this JAR name matches the file generated in your project's target folder.
COPY target/payment-processing-service.jar /app/payment-processing-service.jar

# Document the port used by the Spring Boot application
EXPOSE 8082

# Start the Spring Boot application when the container starts
ENTRYPOINT ["java", "-jar", "/app/payment-processing-service.jar"]
