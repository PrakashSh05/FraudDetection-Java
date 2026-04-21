<div align="center">

<h1>🔐 Fraud Detection API</h1>

<p>A Spring Boot REST API that processes financial transactions and automatically flags suspicious ones in real-time.</p>

[![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.4-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![Tests](https://img.shields.io/badge/Tests-19%20Passing-brightgreen?style=for-the-badge&logo=junit5&logoColor=white)](https://junit.org/junit5/)
[![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)](LICENSE)

</div>

---

## What It Does

Users can create accounts and make transactions. Every transaction is automatically evaluated against fraud rules before it's processed:

- 💳 **Normal transaction** → balance updates, `status: APPROVED`
- 🚨 **Suspicious transaction** → recorded but balance is **not touched**, `status: FLAGGED`

---

## Fraud Rules

| Rule | Condition | Result |
|------|-----------|--------|
| High Amount | Transaction > ₹50,000 | FLAGGED |
| Velocity | More than 3 transactions in 5 minutes | FLAGGED |

> Thresholds are configurable in `application.yml` — no code changes needed.

---

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/users` | Create a user with initial balance |
| `GET` | `/api/users/{id}` | Get user details |
| `POST` | `/api/transactions` | Submit a transaction (fraud check runs automatically) |
| `GET` | `/api/transactions/{id}` | Get a single transaction |
| `GET` | `/api/transactions/user/{userId}` | Get full transaction history for a user |
| `GET` | `/api/fraud/flagged` | Get all flagged transactions |

---

## Quick Demo

```bash
# 1. Clone and run (Java 17+ required)
git clone https://github.com/your-username/fraud-detection-api.git
cd fraud-detection-api
mvn spring-boot:run
```

**Create a user**
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name": "Rahul Sharma", "email": "rahul@example.com", "balance": 100000}'
```

**Make a normal transaction → APPROVED**
```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -d '{"userId": 1, "amount": 5000, "transactionType": "DEBIT"}'
```
```json
{
  "data": {
    "status": "APPROVED",
    "newBalance": 95000.00
  }
}
```

**Try a suspicious amount → FLAGGED**
```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -d '{"userId": 1, "amount": 75000, "transactionType": "DEBIT"}'
```
```json
{
  "data": {
    "status": "FLAGGED",
    "fraudReason": "Amount ₹75000.00 exceeds the allowed limit of ₹50000.00"
  }
}
```

---

## Running Locally

**Prerequisites:** Java 17+, Maven 3.8+

```bash
# Run the app
mvn spring-boot:run

# Run all tests
mvn clean test
```

| URL | What's there |
|-----|-------------|
| `http://localhost:8080/swagger-ui.html` | Interactive API explorer |
| `http://localhost:8080/h2-console` | Live database browser |
| `http://localhost:8080/actuator/health` | Health check |

**H2 Console:** JDBC URL → `jdbc:h2:mem:frauddb` · Username → `sa` · Password → *(blank)*

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Framework | Spring Boot 3.2 |
| Language | Java 17 |
| ORM | Spring Data JPA + Hibernate |
| Database | H2 (in-memory) |
| Validation | Bean Validation / JSR-380 |
| API Docs | Springdoc OpenAPI / Swagger UI |
| Monitoring | Spring Actuator |
| Testing | JUnit 5 + Mockito + MockMvc |
| Build | Maven |

---

## Architecture

```
HTTP Request
     │
     ▼
Controller          (@RestController, @Valid)
     │
     ▼
Service             (@Transactional, fraud logic, balance update)
     │
     ▼
Repository          (Spring Data JPA, custom JPQL queries)
     │
     ▼
H2 Database         (users + transactions tables)
```

Key design decisions:
- **DTO pattern** — entities never exposed directly in API responses
- **`@Transactional(isolation = REPEATABLE_READ)`** — prevents race conditions on concurrent debits
- **Optimistic locking** (`@Version` on User) — detects conflicting concurrent balance updates
- **`@RestControllerAdvice`** — all exceptions handled in one place, consistent error format

---

## Tests

19 tests, 0 failures.

```
TransactionServiceTest          7 tests  — unit (Mockito)
FraudDetectionServiceTest       5 tests  — unit (Mockito)
TransactionControllerTest       7 tests  — integration (MockMvc + H2)
```

Covers: approved debit, FLAGGED high-amount, FLAGGED velocity, insufficient balance, balance unchanged on flag, credit, unknown user 404, validation errors, boundary value at exactly ₹50,000.

---

## Project Structure

```
src/main/java/com/fraudapi/
├── controller/        # REST endpoints
├── service/           # Business logic + fraud detection
├── repository/        # Data access (JPA)
├── model/             # JPA entities
├── dto/               # Request/response objects
├── exception/         # Custom exceptions + global handler
├── config/            # Swagger config
└── constants/         # Status/type string constants
```

---

## License

[MIT](LICENSE) — free to use, modify, and distribute.
