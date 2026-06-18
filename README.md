# Smart Round-Off Micro-Investment Platform (SROMIP)

Event-Driven Fintech Microservices Architecture

---

## Overview

Smart Round-Off Micro-Investment Platform (SROMIP) is a distributed fintech platform that automatically invests spare change generated during digital payments.

The platform processes transactions through an event-driven microservices architecture powered by Apache Kafka. It performs fraud evaluation, OTP-based risk verification, payment execution, automatic round-off investments, notifications, and real-time transaction tracking.

### Example Transaction

```text
Transaction Amount : ₹123.45
Rounded Amount     : ₹124.00
Spare Change       : ₹0.55
Investment Amount  : ₹0.55
```

---

## High-Level System Architecture

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
│ Create Intent • Round-Off Preview     │
│ Idempotency Validation                │
└───────────────────┬───────────────────┘
                    │
                    ▼

         payment-intent-topic

                    │
                    ▼

┌───────────────────────────────────────┐
│            Fraud Service              │
│ Rule Engine • Risk Scoring            │
│ ML Anomaly Detection                  │
│ APPROVED • OTP_REQUIRED • BLOCKED     │
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
│ Spare Change Investment               │
│ Portfolio Updates                     │
└───────────────────┬───────────────────┘
                    │
                    ▼

      investment-completed-topic

                    │
                    ▼

┌───────────────────────────────────────┐
│       Notification Service            │
│ Payment • Investment • Fraud Alerts   │
└───────────────────┬───────────────────┘
                    │
                    ▼

           notification-topic

                    │
                    ▼

┌───────────────────────────────────────┐
│         Dashboard Service             │
│ Analytics • Fraud Metrics             │
│ Investment Insights                   │
└───────────────────────────────────────┘
```

---

## Event Flow

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

## Microservices

| Service | Responsibility |
|----------|---------------|
| Auth Service | User Registration, Login, JWT Management |
| API Gateway | Routing, Security, Rate Limiting |
| Payment Intent Service | Payment Intent Creation and Round-Off Preview |
| Fraud Service | Fraud Detection and Risk Evaluation |
| Payment Service | Transaction Orchestration |
| OTP Service | OTP Generation and Verification |
| Investment Service | Spare Change Investment |
| Notification Service | User Notifications |
| Dashboard Service | Analytics and Monitoring |

---

## Kafka Topics

### Main Processing Pipeline

```text
payment-intent-topic
trust-decision-topic
payment-topic
investment-completed-topic
notification-topic
```

### OTP Pipeline

```text
otp-request-topic
otp-verified-topic
```

---

## Database Architecture

| Service | Tables |
|----------|---------|
| Auth Service | users |
| Payment Intent Service | payment_intents, idempotency_keys |
| Fraud Service | fraud_checks, risk_scores |
| Payment Service | payments, user_preferences |
| Investment Service | investments |
| Notification Service | notifications |
| Dashboard Service | dashboard_transactions |

---

## Technology Stack

### Backend

```text
Java 21
Spring Boot
Spring Security
Spring Cloud Gateway
Spring Data JPA
Spring Kafka
Gradle
```

### Infrastructure

```text
Apache Kafka
Redis
PostgreSQL
Docker
Docker Compose
Eureka Service Discovery
```

### Fraud Detection

```text
FastAPI
Scikit-Learn
Isolation Forest
```

---

## Project Structure

```text
smart-roundoff-microinvestment-platform
│
├── api-gateway
├── auth-service
├── payment-intent-service
├── fraud-service
├── payment-service
├── otp-service
├── investment-service
├── notification-service
├── dashboard-service
│
├── common-libs
│
├── docker-compose.yml
├── start-all.sh
├── stop-all.sh
└── README.md
```

---

## Quick Start

### Clone Repository

```bash
git clone https://github.com/karthiksai15/smart-roundoff-microinvestment-platform.git

cd smart-roundoff-microinvestment-platform
```

### Start Infrastructure

```bash
docker compose up -d
```

### Build Project

```bash
./gradlew clean build
```

### Start All Services

```bash
./start-all.sh
```

---

## Key Features

- Event-Driven Microservices Architecture
- Apache Kafka-Based Messaging
- JWT Authentication and Authorization
- API Gateway Security
- Fraud Detection and Risk Evaluation
- OTP-Based Verification Flow
- Automatic Round-Off Investments
- Real-Time Notifications
- Dashboard Analytics
- Redis Caching
- PostgreSQL Persistence
- Service Discovery with Eureka
- Idempotent Request Processing
- Circuit Breaker and Retry Support

---

## Future Enhancements

### Observability

- Prometheus
- Grafana
- ELK Stack

### Distributed Tracing

- OpenTelemetry
- Zipkin

### Kafka Reliability

- Dead Letter Queues (DLQ)
- Retry Topics
- Event Replay Support

### Event Serialization

- Apache Avro
- Schema Registry

### Cloud Deployment

- AWS
- Kubernetes
- CI/CD Pipelines

### Frontend

- React Dashboard
- Portfolio Analytics
- Real-Time Charts

---

## Project Classification

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

## Concepts Demonstrated

- Event-Driven Architecture
- Distributed Systems Design
- Kafka Messaging
- Microservices Communication
- Fraud Detection Workflows
- OTP Verification Flows
- JWT Security
- API Gateway Patterns
- Service Discovery
- Redis Caching
- Database Isolation
- Idempotent APIs
- Fault Tolerance
- Payment Orchestration
- Investment Automation

---

## Conclusion

SROMIP demonstrates a production-style fintech workflow built using an event-driven microservices architecture. The platform integrates authentication, fraud evaluation, OTP verification, payment orchestration, automatic round-off investments, notifications, and analytics through asynchronous Kafka-based communication while maintaining scalability, reliability, and service independence.
>>>>>>> a417b21 (modified dockor compose)
