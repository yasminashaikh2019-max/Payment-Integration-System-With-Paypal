# PayPal Payment Integration System

A microservices-based payment integration system built using Java and Spring Boot, integrating with PayPal for payment order creation and capture.

## Project Overview

This project demonstrates how a merchant application can integrate with PayPal through independent Spring Boot microservices.

The system separates payment processing from PayPal-specific provider communication, making the application easier to maintain and extend.

## Architecture

The project contains three independent services:

1. **Payment Processing Service**
   - Handles payment initiation and transaction processing.
   - Manages transaction statuses.
   - Communicates with the PayPal Provider Service.
   - Uses JDBC to persist transaction information.

2. **PayPal Provider Service**
   - Integrates with PayPal REST APIs.
   - Handles OAuth 2.0 authentication.
   - Creates PayPal orders.
   - Captures approved PayPal orders.
   - Processes provider responses and errors.

3. **Eureka Service Registry**
   - Provides service discovery.
   - Allows microservices to register and discover each other.

## Payment Flow

1. Customer initiates a payment.
2. Payment Processing Service receives the payment request.
3. The processing service communicates with the PayPal Provider Service.
4. PayPal Provider Service obtains an OAuth access token.
5. A PayPal order is created.
6. Customer approves the PayPal payment.
7. The provider service captures the order.
8. The processing service updates the transaction status in the database.

## Technologies Used

- Java 17
- Spring Boot
- Spring MVC and REST APIs
- Spring JDBC / NamedParameterJdbcTemplate
- MySQL
- PayPal REST APIs
- OAuth 2.0
- Netflix Eureka
- Resilience4j Circuit Breaker
- Spring Boot Actuator
- Apache Kafka
- Maven
- Docker

## Transaction Status Management

The processing service maintains transaction statuses such as:

- CREATED
- INITIATED
- PENDING
- APPROVED
- SUCCESS
- FAILED

## Design Patterns and Reliability

- Factory Design Pattern for transaction status processing.
- Circuit Breaker using Resilience4j.
- Environment-based configuration for credentials.
- Transaction reference handling to support payment tracking and duplicate-request protection.

## Service Ports

| Service | Port |
|---|---:|
| Payment Processing Service | 8082 |
| PayPal Provider Service | 8083 |
| Eureka Service Registry | 8761 |

## Configuration

Sensitive configuration values should be provided through environment variables.

Examples:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `P_CLIENT_ID`
- `P_CLIENT_SECRET`

Do not commit actual credentials, access tokens, or `.env` files to GitHub.

## Repository Structure

```text
Payment-Integration-System-With-Paypal/
├── payment-processing-service/
├── paypal-provider-service/
├── eureka-service-registry/
└── .gitignore
```

## Learning Objectives

This project demonstrates practical implementation of:

- Microservices architecture
- Third-party payment gateway integration
- REST API communication
- OAuth 2.0 authentication
- Database transaction management
- Service discovery
- Circuit breaker implementation
- Docker-based application deployment

## Author

**Yasmin Shaikh**

Java Backend Developer

