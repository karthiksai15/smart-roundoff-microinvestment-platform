# Smart Round-Off Micro-Investment Platform (SROMIP)

## Event-Driven Fintech Microservices Platform

SROMIP is a distributed fintech platform that automatically invests spare change generated during digital payments. The platform follows an event-driven microservices architecture built using Spring Boot, Apache Kafka, Redis, PostgreSQL, Docker, and Spring Cloud.

The objective of the platform is to demonstrate how modern payment systems can process transactions, evaluate fraud risk, perform OTP verification, execute automated investments, send notifications, and maintain real-time analytics using asynchronous communication between services.

### Example Transaction

```text
Transaction Amount : ₹123.45
Rounded Amount     : ₹124.00
Spare Change       : ₹0.55
Investment Amount  : ₹0.55
```

In this example:

```text
User Pays          → ₹123.45
System Rounds To   → ₹124.00
Spare Change       → ₹0.55
Investment Created → ₹0.55
```

The platform automatically captures the spare amount and creates a micro-investment without requiring additional user interaction.

---

# Key Features

* JWT Authentication and Authorization
* API Gateway Routing and Security
* Payment Intent Management
* Fraud Detection and Risk Evaluation
* OTP-Based Verification Flow
* Automatic Round-Off Investments
* Kafka Event-Driven Communication
* Redis Caching
* PostgreSQL Persistence
* Notification Processing
* Dashboard Analytics
* Service Discovery with Eureka
* Circuit Breaker and Retry Mechanisms
* Idempotent Request Processing

---

# High-Level System Architecture

```text
┌───────────────────────────────────────┐
│          Client Application           │
│ Login → Scan QR → Pay → Confirm       │
└───────────────────┬───────────────────┘
                    │
                    ▼
┌───────────────────────────────────────┐
│              API Gateway              │
│ JWT Filter • Rate Limiter • Routing   │
└───────────────────┬───────────────────┘
                    │
                    ▼
┌───────────────────────────────────────┐
│             Auth Service              │
│ Registration • Login • JWT            │
└───────────────────┬───────────────────┘
                    │
                    ▼
┌───────────────────────────────────────┐
│       Payment Intent Service          │
│ • Create Payment Intent               │
│ • Round-Off Preview                   │
│ • Idempotency Validation              │
└───────────────────┬───────────────────┘
                    │
                    ▼

         payment-intent-topic

                    │
                    ▼

┌───────────────────────────────────────┐
│            Fraud Service              │
│ • Rule Engine                         │
│ • Risk Scoring                        │
│ • Trust Evaluation                    │
│                                       │
│ APPROVED                              │
│ OTP_REQUIRED                          │
│ BLOCKED                               │
└───────────────────┬───────────────────┘
                    │
                    ▼

         trust-decision-topic

                    │
                    ▼

┌───────────────────────────────────────┐
│           Payment Service             │
│                                       │
│ APPROVED     → Execute Payment        │
│ OTP_REQUIRED → Trigger OTP Flow       │
│ BLOCKED      → Reject Payment         │
└──────────────┬───────────────┬────────┘
               │               │
               │               ▼
               │      otp-request-topic
               │               │
               │               ▼
               │    ┌──────────────────┐
               │    │   OTP Service    │
               │    │ Generate OTP     │
               │    │ Verify OTP       │
               │    └────────┬─────────┘
               │             │
               └─────────────┘
                             │
                             ▼

                    otp-verified-topic

                             │
                             ▼

                    Payment Completed

                             │
                             ▼

                        payment-topic

                             │
                             ▼

┌───────────────────────────────────────┐
│         Investment Service            │
│ • Spare Change Investment             │
│ • Portfolio Updates                   │
└───────────────────┬───────────────────┘
                    │
                    ▼

      investment-completed-topic

                    │
                    ▼

┌───────────────────────────────────────┐
│       Notification Service            │
│ • Payment Notifications               │
│ • Investment Notifications            │
│ • Fraud Notifications                 │
└───────────────────┬───────────────────┘
                    │
                    ▼

           notification-topic

                    │
                    ▼

┌───────────────────────────────────────┐
│         Dashboard Service             │
│ • Transaction Analytics               │
│ • Fraud Metrics                       │
│ • Investment Metrics                  │
└───────────────────────────────────────┘
```

---

# Event Flow

```text
Client
  │
  ▼

Payment Intent Service
  │
  ▼

payment-intent-topic
  │
  ▼

Fraud Service
  │
  ▼

trust-decision-topic
  │
  ▼

Payment Service

  ├── APPROVED
  │        │
  │        ▼
  │   Execute Payment
  │
  ├── OTP_REQUIRED
  │        │
  │        ▼
  │   otp-request-topic
  │        │
  │        ▼
  │     OTP Service
  │        │
  │        ▼
  │   otp-verified-topic
  │        │
  │        ▼
  │   Resume Payment
  │
  └── BLOCKED
           │
           ▼
      Notification

           │
           ▼

payment-topic
  │
  ▼

Investment Service
  │
  ▼

investment-completed-topic
  │
  ▼

Notification Service
  │
  ▼

notification-topic
  │
  ▼

Dashboard Service
```

---

# Microservices

| Service                | Responsibility                                                   |
| ---------------------- | ---------------------------------------------------------------- |
| Auth Service           | User Registration, Login, JWT Management                         |
| API Gateway            | Request Routing, Authentication Filter, Rate Limiting            |
| Payment Intent Service | Payment Intent Creation, Round-Off Preview, Idempotency Handling |
| Fraud Service          | Fraud Detection and Risk Evaluation                              |
| Payment Service        | Payment Processing and Workflow Orchestration                    |
| OTP Service            | OTP Generation and Verification                                  |
| Investment Service     | Spare Change Investments                                         |
| Notification Service   | Notification Processing                                          |
| Dashboard Service      | Analytics and Reporting                                          |

---

# Kafka Topics

## Payment Processing Pipeline

```text
payment-intent-topic
trust-decision-topic
payment-topic
investment-completed-topic
notification-topic
```

## OTP Verification Pipeline

```text
otp-request-topic
otp-verified-topic
```
---

# Database Architecture

Each microservice maintains its own database schema to ensure loose coupling and service independence.

| Service                | Database Tables                   |
| ---------------------- | --------------------------------- |
| Auth Service           | users                             |
| Payment Intent Service | payment_intents, idempotency_keys |
| Fraud Service          | fraud_checks, risk_scores         |
| Payment Service        | payments, user_preferences        |
| Investment Service     | investments                       |
| Notification Service   | notifications                     |
| Dashboard Service      | dashboard_transactions            |

---

# Technology Stack

## Backend Technologies

```text
Java 21
Spring Boot
Spring Security
Spring Data JPA
Spring Cloud Gateway
Spring Kafka
Spring Validation
Gradle
```

## Infrastructure

```text
Apache Kafka
Redis
PostgreSQL
Docker
Docker Compose
Eureka Service Discovery
```

## Fraud Detection

```text
FastAPI
Scikit-Learn
Isolation Forest
```

---

# Architectural Decisions

## Event-Driven Communication

Services communicate asynchronously through Kafka topics rather than direct service-to-service calls.

Benefits:

* Loose coupling between services
* Improved scalability
* Better fault isolation
* Independent service deployment
* Event replay capability

---

## Idempotency Support

Payment Intent requests support idempotency keys to prevent duplicate transaction creation.

```text
Client Request
      │
      ▼
Idempotency Check
      │
      ├── Existing Response Found
      │         │
      │         ▼
      │   Return Cached Response
      │
      └── New Request
                │
                ▼
        Process Transaction
```

This prevents duplicate payments caused by retries, network failures, or repeated client submissions.

---

## Fraud Evaluation Strategy

Transactions are classified into three risk categories.

### Low Risk

```text
Transaction
     │
     ▼
Approved
     │
     ▼
Execute Payment
```

### Medium Risk

```text
Transaction
     │
     ▼
OTP Required
     │
     ▼
Verify OTP
     │
     ▼
Execute Payment
```

### High Risk

```text
Transaction
     │
     ▼
Blocked
     │
     ▼
Reject Transaction
```

---

## OTP Verification Workflow

Medium-risk transactions enter an OTP verification stage before payment execution.

```text
Payment Service
      │
      ▼
otp-request-topic
      │
      ▼
OTP Service
      │
      ▼
Generate OTP
      │
      ▼
User Verification
      │
      ▼
otp-verified-topic
      │
      ▼
Payment Service
      │
      ▼
Complete Payment
```

---

# Project Structure

```text
smart-roundoff-microinvestment-platform
│
├── api-gateway
│
├── auth-service
│
├── payment-intent-service
│
├── fraud-service
│
├── payment-service
│
├── otp-service
│
├── investment-service
│
├── notification-service
│
├── dashboard-service
│
├── common-libs
│
├── docker-compose.yml
│
├── start-all.sh
│
├── stop-all.sh
│
└── README.md
```

---

# Local Development Setup

## Clone Repository

```bash
git clone https://github.com/karthiksai15/smart-roundoff-microinvestment-platform.git

cd smart-roundoff-microinvestment-platform
```

---

## Start Infrastructure

The platform uses Docker containers for PostgreSQL, Kafka, Zookeeper, and Redis.

```bash
docker compose up -d
```

Verify running containers:

```bash
docker ps
```

---

## Build Entire Project

```bash
./gradlew clean build
```

---

## Start Services

### Eureka Server

```bash
./gradlew :eureka-server:bootRun
```

### API Gateway

```bash
./gradlew :api-gateway:bootRun
```

### Auth Service

```bash
./gradlew :auth-service:bootRun
```

### Payment Intent Service

```bash
./gradlew :payment-intent-service:bootRun
```

### Fraud Service

```bash
./gradlew :fraud-service:bootRun
```

### Payment Service

```bash
./gradlew :payment-service:bootRun
```

### OTP Service

```bash
./gradlew :otp-service:bootRun
```

### Investment Service

```bash
./gradlew :investment-service:bootRun
```

### Notification Service

```bash
./gradlew :notification-service:bootRun
```

### Dashboard Service

```bash
./gradlew :dashboard-service:bootRun
```

---

# Health Checks

## API Gateway

```bash
curl http://localhost:9000/actuator/health
```

## Payment Intent Service

```bash
curl http://localhost:8085/actuator/health
```

## Eureka Dashboard

```text
http://localhost:8761
```

---

# Database Verification Commands

## Payment Intent Database

```sql
SELECT COUNT(*) FROM payment_intents;
SELECT COUNT(*) FROM idempotency_keys;
```

## Fraud Database

```sql
SELECT COUNT(*) FROM fraud_checks;
SELECT COUNT(*) FROM risk_scores;
```

## Payment Database

```sql
SELECT COUNT(*) FROM payments;
```

## Investment Database

```sql
SELECT COUNT(*) FROM investments;
```

## Notification Database

```sql
SELECT COUNT(*) FROM notifications;
```

## Dashboard Database

```sql
SELECT COUNT(*) FROM dashboard_transactions;
```
---

# End-to-End Workflows

The platform supports three transaction paths based on fraud risk evaluation.

---

## Low-Risk Transaction Flow

```text
Client
  │
  ▼
Payment Intent Created
  │
  ▼
Fraud Evaluation
  │
  ▼
APPROVED
  │
  ▼
Payment Executed
  │
  ▼
Investment Triggered
  │
  ▼
Notification Sent
  │
  ▼
Dashboard Updated
```

### Example

```text
Amount          : ₹123.45
Rounded Amount  : ₹124.00
Spare Amount    : ₹0.55

Risk Level      : LOW
Payment Status  : COMPLETED
Investment      : EXECUTED
```

---

## Medium-Risk Transaction Flow

```text
Client
  │
  ▼
Payment Intent Created
  │
  ▼
Fraud Evaluation
  │
  ▼
OTP_REQUIRED
  │
  ▼
Generate OTP
  │
  ▼
User Verification
  │
  ▼
OTP Verified
  │
  ▼
Payment Completed
  │
  ▼
Investment Triggered
  │
  ▼
Notification Sent
  │
  ▼
Dashboard Updated
```

### Example

```text
Amount          : ₹200000.75

Risk Level      : MEDIUM
OTP Required    : YES
OTP Verified    : YES

Payment Status  : COMPLETED
Investment      : EXECUTED
```

---

## High-Risk Transaction Flow

```text
Client
  │
  ▼
Payment Intent Created
  │
  ▼
Fraud Evaluation
  │
  ▼
BLOCKED
  │
  ▼
Reject Payment
  │
  ▼
Notification Sent
  │
  ▼
Dashboard Updated
```

### Example

```text
Amount          : ₹999999

Risk Level      : HIGH
Payment Status  : FAILED
Investment      : NOT CREATED
```

---

# API Testing

## Register User

```bash
curl -X POST http://localhost:9000/auth/register
```

---

## Login

```bash
curl -X POST http://localhost:9000/auth/login
```

---

## Create Payment Intent

```bash
curl -X POST http://localhost:9000/api/payment-intent/create
```

---

## Verify OTP

```bash
curl -X POST http://localhost:9000/api/otp/verify
```

---

# Security Features

### Authentication

* JWT Access Tokens
* JWT Refresh Tokens
* Token Validation at Gateway
* Role-Based Access Support

### Gateway Protection

* JWT Authentication Filter
* Rate Limiting
* Circuit Breaker
* Retry Mechanism
* Centralized Routing

### Fraud Protection

* Risk-Based Evaluation
* Transaction Classification
* OTP Escalation for Medium-Risk Transactions
* High-Risk Transaction Blocking

### Data Protection

* PostgreSQL Persistence
* Redis Session Storage
* Idempotency Validation
* Duplicate Request Prevention

---

# Implemented Features

### Core Platform

* User Registration and Login
* JWT Authentication
* Payment Intent Creation
* Round-Off Calculation
* Spare Change Investment

### Event-Driven Processing

* Kafka-Based Communication
* Asynchronous Service Interaction
* Topic-Based Workflow Processing

### Fraud Detection

* Fraud Evaluation Pipeline
* Risk Scoring
* Low / Medium / High Classification

### OTP Verification

* OTP Generation
* OTP Validation
* OTP Retry Support
* OTP Expiry Handling

### Payment Processing

* Payment Execution
* OTP Resume Flow
* Fraud Rejection Flow

### Investment Processing

* Investment Creation
* Duplicate Investment Prevention
* Investment Event Publishing

### Notification Processing

* Payment Notifications
* Fraud Notifications
* Investment Notifications

### Dashboard Analytics

* Transaction Tracking
* Fraud Visibility
* Investment Metrics

### Reliability Features

* Idempotent APIs
* Redis Caching
* Circuit Breaker Support
* Retry Policies
* Service Discovery

---

# Current Project Status

The platform has been successfully tested for:

```text
User Registration
User Login
JWT Authentication

Low-Risk Transaction Flow
Medium-Risk OTP Flow
High-Risk Block Flow

Fraud Processing
OTP Verification
Payment Execution

Investment Creation
Notification Processing
Dashboard Updates

Kafka Event Processing
Redis Integration
PostgreSQL Persistence
```

---

# Future Enhancements

### Observability

* Prometheus
* Grafana
* ELK Stack
* Centralized Log Aggregation

### Distributed Tracing

* OpenTelemetry
* Zipkin
* End-to-End Request Tracing

### Kafka Reliability

* Dead Letter Queues (DLQ)
* Retry Topics
* Replay Support
* Event Auditing

### Event Serialization

* Apache Avro
* Schema Registry

### Cloud Deployment

* AWS
* Kubernetes
* Auto Scaling
* Infrastructure as Code

### Frontend

* React Dashboard
* Portfolio View
* Real-Time Charts
* Transaction Monitoring

---

# Project Classification

```text
Domain             : Fintech

Architecture       : Event-Driven Microservices

Communication      : Apache Kafka

Database           : PostgreSQL

Caching            : Redis

Security           : JWT Authentication

Service Discovery  : Eureka

Complexity         : Advanced

Deployment         : Docker
```

---

# Concepts Demonstrated

* Event-Driven Architecture
* Microservices Design
* Distributed Systems
* Kafka Messaging
* Fraud Detection Workflows
* OTP Verification Flows
* JWT Security
* API Gateway Patterns
* Service Discovery
* Redis Caching
* Database Isolation
* Idempotent APIs
* Fault Tolerance
* Payment Orchestration
* Investment Automation

---

# Conclusion

Smart Round-Off Micro-Investment Platform (SROMIP) demonstrates a production-style fintech workflow built using an event-driven microservices architecture. The platform integrates authentication, fraud evaluation, OTP verification, payment orchestration, automatic round-off investments, notifications, and analytics through asynchronous Kafka-based communication while maintaining service independence, scalability, and fault tolerance.
