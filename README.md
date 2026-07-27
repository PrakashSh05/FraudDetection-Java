# Transaction Risk Analysis Platform V2 🛡️

[![CI/CD Pipeline](https://github.com/PrakashSh05/FraudDetection-Java/actions/workflows/ci.yml/badge.svg)](https://github.com/PrakashSh05/FraudDetection-Java/actions/workflows/ci.yml)
[![Java 21](https://img.shields.io/badge/Java-21-orange.svg)](https://jdk.java.net/21/)
[![Spring Boot 3](https://img.shields.io/badge/Spring_Boot-3.2.4-green.svg)](https://spring.io/projects/spring-boot)
[![React 18](https://img.shields.io/badge/React-18-blue.svg)](https://react.dev/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

An enterprise-grade, high-throughput **Real-Time Fraud Detection & Risk Analysis Platform** built using Java 21, Spring Boot 3.x, React 18, TanStack React Query v5, Recharts, and Docker.

---

## 📌 Problem Statement & Solution

### The Challenge
High-volume payment networks face sophisticated fraud attempts. Legacy detection architectures rely on monolithic conditional checks that are difficult to extend, lack audit trail capabilities, and block compliance analysts from performing manual reviews effectively.

### The Solution
The **Transaction Risk Analysis Platform V2** provides:
1. **Pluggable Strategy Pattern Rule Engine**: Add new fraud indicators without touching orchestration logic.
2. **Automated Risk Scoring & Decision Mapping**: Capped risk scores (100 max) dynamically mapped to risk tiers (`LOW`, `MEDIUM`, `HIGH`, `CRITICAL`) and automated execution policies (`APPROVED`, `MONITOR`, `REVIEW`, `REJECTED`).
3. **Operational Case Management & Immutable Audit Timeline**: Automated manual review case creation (`FraudCase`) for flagged transactions backed by a write-once audit history (`FraudCaseAudit`).
4. **Executive Compliance Dashboard**: Real-time React 18 dashboard featuring interactive risk telemetry, queue metrics, and investigation workspaces.

---

## 📸 Platform Preview

| Executive Risk Dashboard | Analyst Case Queue |
| :---: | :---: |
| *(Dashboard Telemetry, Charts & Widgets)* | *(Paginated, Sortable, Filterable Queue)* |

---

## 🛠️ Technology Stack

| Domain | Technologies |
| :--- | :--- |
| **Backend Core** | Java 21, Spring Boot 3.2.4, Spring Data JPA, Spring Validation |
| **Database & Migration** | Flyway DB Migration, H2 In-Memory / Relational DB |
| **Architecture** | Strategy Pattern, Domain-Driven Design, Java Records |
| **Frontend Core** | React 18, TypeScript, Vite, Tailwind CSS, Lucide Icons |
| **State & API** | TanStack React Query v5, Axios |
| **Data Visualization** | Recharts (Doughnut, Line, Horizontal Bar) |
| **Containerization** | Docker, Multi-Stage Dockerfiles, Nginx Alpine, Docker Compose |
| **CI/CD & Testing** | GitHub Actions, JUnit 5, MockMvc, JaCoCo, Vitest, React Testing Library |

---

## 🚀 One-Command Startup (Docker Compose)

### Prerequisites
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) (includes Docker Compose)

### Quick Start
To launch the complete application stack (Backend API + Frontend Nginx SPA) with a single command:

```bash
docker compose up --build
```

Access the applications once containers are healthy:
- **Frontend Dashboard**: [http://localhost:3000](http://localhost:3000)
- **Backend Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **Actuator Health**: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)

---

## 📚 Documentation Index

- 🏗️ [System Architecture & Diagrams](docs/architecture.md)
- 📖 [REST API Documentation](docs/api.md)
- 💻 [Developer & Contribution Guide](docs/development.md)

---

## 📄 License & Author

This project is open-source under the [MIT License](LICENSE).

Developed by **Prakash Sharma**.
