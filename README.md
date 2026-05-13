# Smart Round-Off Micro-Investment Platform

This project is a distributed microservices-based fintech platform that simulates a round-off investment system.

When a user makes a transaction, the platform automatically rounds off the amount and invests the spare change into a micro-investment account. The system is built using an event-driven microservices architecture with Kafka for asynchronous communication between services.

The main goal of this project was to explore distributed systems concepts such as microservices communication, idempotency, fault tolerance, OTP workflows, fraud detection, centralized logging, and event-driven processing.

---

# System Workflow

```text
┌──────────────┐
│    CLIENT    │
│ Web / Mobile │
└──────┬───────┘
       │
       ▼
┌─────────────────────┐
│     API GATEWAY     │
│ JWT | Rate Limit    │
│ Routing | Filters   │
└─────────┬───────────┘
          │
          ▼
┌──────────────────────────┐
│ PAYMENT INTENT SERVICE   │
│ - Create payment intent  │
│ - Calculate round-off    │
│ - Handle idempotency     │
└──────────┬───────────────┘
           │
           ▼
┌──────────────────────────┐
│      FRAUD SERVICE       │
│ - Risk analysis          │
│ - ML-based validation    │
└──────────┬───────────────┘
           │
           ▼
┌──────────────────────────┐
│     PAYMENT SERVICE      │
│ Approve / Block / OTP    │
└───────┬────────┬─────────┘
        │        │
        │        └──────────────┐
        │                       │
        ▼                       ▼
┌────────────────┐   ┌────────────────────┐
│   OTP SERVICE  │   │ INVESTMENT SERVICE │
│ OTP Verification│  │ Invest spare amount│
└────────┬───────┘   └─────────┬──────────┘
         │                     │
         └──────────┬──────────┘
                    ▼
         ┌────────────────────┐
         │ NOTIFICATION       │
         │ SERVICE            │
         └─────────┬──────────┘
                   │
                   ▼
         ┌────────────────────┐
         │ DASHBOARD SERVICE  │
         │ Real-time tracking │
         └────────────────────┘
```

---

# Event Flow

```text
Payment Intent Created
        │
        ▼
Fraud Evaluation
        │
        ▼
Payment Processing
        │
 ┌──────┴────────┐
 │               │
 ▼               ▼
OTP Flow      Approved
 │               │
 ▼               ▼
OTP Verified   Investment Triggered
        │
        ▼
Notification Sent
        │
        ▼
Dashboard Updated
```

---

# Architecture Overview

The platform follows a distributed microservices architecture where each service handles a single responsibility.

### Communication Style

```text
REST API  → synchronous communication
Kafka     → asynchronous event communication
```

### Main Services

* API Gateway
* Auth Service
* Payment Intent Service
* Fraud Service
* Payment Service
* OTP Service
* Investment Service
* Notification Service
* Dashboard Service
* Eureka Service Registry

---

# Infrastructure Layer

```text
Kafka        → event-driven communication
Redis        → idempotency, OTP storage, caching, rate limiting
PostgreSQL   → persistent storage
Eureka       → service discovery
ELK Stack    → centralized logging
Docker       → containerized deployment
```

---

# Technologies Used

### Backend

* Java
* Spring Boot
* FastAPI

### Distributed Systems & Messaging

* Apache Kafka
* Eureka Service Registry

### Databases & Caching

* PostgreSQL
* Redis

### DevOps & Monitoring

* Docker
* ELK Stack
* Gradle

---

# Key Features

* Event-driven architecture using Kafka
* Distributed microservices design
* JWT authentication and API Gateway security
* Redis-based idempotency handling
* OTP verification workflow
* Fraud detection integration
* DLQ and retry mechanisms
* Circuit breaker and fault tolerance
* Real-time dashboard aggregation
* Centralized logging using ELK

---

# Technical Concepts Implemented

* Distributed Systems
* Event-Driven Architecture
* Asynchronous Processing
* Microservices
* CQRS-style Dashboard Aggregation
* Idempotency
* Fault Tolerance
* Retry and Dead Letter Queue Handling
* Centralized Logging and Observability

---

# Running the Project

```bash
docker-compose up --build
```

---

# Future Improvements

* Kubernetes deployment
* CI/CD pipeline integration
* Prometheus and Grafana monitoring
* Distributed tracing
* API Gateway improvements
