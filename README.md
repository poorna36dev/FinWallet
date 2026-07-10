# 💳 Fintech Backend API

A production-oriented fintech backend built with **Spring Boot**, focusing on secure money transfers, reliability, concurrency handling, event-driven architecture, and production engineering practices.

Unlike a typical CRUD application, this project explores the engineering challenges encountered while building financial systems, including concurrent updates, duplicate request protection, asynchronous processing, caching, observability, security hardening, and operational reliability.

---

# Features

## Authentication & Security

- JWT Authentication & Authorization
- Email Verification
- Password Reset
- Email Change Verification
- BCrypt Password Encryption
- Redis-based Rate Limiting
- Ownership-based Authorization
- Request Correlation IDs (MDC)

---

## Wallet Operations

- Create Wallet
- Deposit
- Withdraw
- Transfer Money
- Transaction History
- Pagination

---

## Reliability

- Idempotent Transfer API
- SHA-256 Request Hash Validation
- Duplicate Request Protection
- Transaction-safe Retry Handling
- Global Exception Handling
- Bean Validation

---

## Concurrency

- Optimistic Locking
- Deadlock Prevention Strategy
- Repository-level Ownership Verification

---

## Redis

- Spring Cache Integration
- JSON Serialization
- DTO-based Caching
- Cache TTL
- Cache Eviction Strategy
- Cache Consistency

---

## Event-Driven Architecture

- Spring Events
- Transactional Event Listeners
- Asynchronous Processing
- Notification Persistence
- Email Notifications

---

## Background Processing

- Retry Scheduler
- Batch Processing
- Pagination-based Scheduler
- Partial Failure Recovery
- Graceful Shutdown

---

## Observability

- AOP Logging
- Execution Time Logging
- Business Event Logging
- Request Correlation IDs
- Micrometer Metrics
- Spring Boot Actuator
- Health Monitoring

---

## Performance

- Query Performance Analysis
- DTO Projections
- Database Indexing
- Connection Pooling (HikariCP)
- Transaction Optimization
- N+1 Query Investigation

---

## API Documentation

- OpenAPI
- Swagger UI
- JWT Authentication Support in Swagger

---

# Architecture

```text
                HTTP Request
                     │
                     ▼
            Security Filter Chain
                     │
                     ▼
             Authentication
                     │
                     ▼
              Authorization
                     │
                     ▼
              Request Logging
                     │
                     ▼
           Idempotency Aspect
                     │
                     ▼
             Business Service
                     │
         ┌───────────┴────────────┐
         ▼                        ▼
      MySQL                   Redis Cache
         │
         ▼
 Publish Business Event
         │
         ▼
@TransactionalEventListener
         │
         ▼
       @Async
         │
         ▼
 Notification Service
         │
         ▼
      Brevo Email API
```

---

# Tech Stack

## Backend

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate

## Database

- MySQL
- Redis

## Security

- JWT
- BCrypt

## Documentation

- SpringDoc OpenAPI
- Swagger UI

## Monitoring

- Micrometer
- Spring Boot Actuator
- SLF4J
- Logback

## Build

- Maven

---

# Engineering Highlights

## Concurrency Control

Implemented optimistic locking using `@Version` and designed a deterministic locking strategy to prevent deadlocks during wallet transfers.

---

## Idempotency

Financial transfers are protected against duplicate execution using:

- Client-generated Idempotency Keys
- SHA-256 Request Hashing
- Persistent Idempotency Records
- Transaction-safe Processing

---

## Event-Driven Design

Business operations publish events instead of directly invoking external systems.

Benefits:

- Loose coupling
- Faster response times
- Better scalability
- Failure isolation

---

## Notification System

Notifications are persisted before delivery and processed asynchronously.

Features include:

- Retry Scheduling
- Batch Processing
- Retry Limits
- Failure Recovery

---

## Caching

Implemented Redis caching using Spring Cache.

Features:

- DTO Serialization
- TTL
- Cache Eviction
- Cache Consistency
- JSON Serialization

---

## Observability

The application provides production-style observability through:

- Request Correlation IDs
- Structured Logging
- Execution Time Metrics
- Business Events
- Health Endpoints

---

# Security Features

- JWT Authentication
- Ownership Enforcement
- Email Verification
- Password Reset
- Rate Limiting
- Sensitive Data Masking in Logs
- Bean Validation
- Global Exception Handling

---

# Project Structure

```text
src
 ├── config
 ├── controller
 ├── dto
 ├── entity
 ├── repository
 ├── service
 ├── security
 ├── cache
 ├── event
 ├── listener
 ├── scheduler
 ├── aspect
 ├── validation
 ├── exception
 ├── metrics
 └── util
```

---

# Running the Project

## Prerequisites

- Java 21
- Maven
- MySQL
- Redis

Configure the required environment variables before starting the application.

Example:

```properties
DB_URL=
DB_USERNAME=
DB_PASSWORD=
JWT_SECRET=
BREVO_API_KEY=
```

Run:

```bash
mvn spring-boot:run
```

Swagger:

```
http://localhost:8080/swagger-ui/index.html
```

---

# Future Improvements

- Docker & Docker Compose
- GitHub Actions CI
- Deployment
- Comprehensive Automated Testing
- Distributed Messaging (Kafka)
- Cloud Deployment

---

# What I Learned

This project was built incrementally by solving real engineering problems rather than implementing framework features in isolation.

Major topics explored include:

- Secure Authentication
- Transaction Management
- Concurrency Control
- Retry-safe APIs
- Event-driven Architecture
- Redis Caching
- Background Processing
- Observability
- Performance Engineering
- Production Security
- API Design

Each feature was introduced to solve a concrete problem encountered while evolving the application, mirroring the way production backend systems typically grow.
