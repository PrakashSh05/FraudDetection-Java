# Developer Guide & Contribution Handbook 💻

Welcome to the **Transaction Risk Analysis Platform V2** developer documentation!

---

## 📂 Project Structure

```
FraudDetection-Java/
├── .github/workflows/       # GitHub Actions CI/CD pipeline
├── docs/                    # Architecture, API & Development Guides
├── frontend/                # React 18 SPA (Vite + TypeScript + Tailwind)
│   ├── src/
│   │   ├── app/             # React Router config & Providers
│   │   ├── components/ui/   # Reusable UI primitives
│   │   ├── features/        # Feature modules (dashboard, cases, investigation, caseDetail)
│   │   ├── layout/          # MainLayout, Sidebar, Header
│   │   ├── lib/             # Axios client, QueryClient, themes
│   │   └── utils/           # Date & string helpers
│   ├── Dockerfile
│   └── nginx.conf
├── src/
│   ├── main/
│   │   ├── java/com/fraudapi/
│   │   │   ├── config/      # FraudRuleProperties configuration
│   │   │   ├── constants/   # Enums (RiskLevel, Decision, FraudCaseStatus, etc.)
│   │   │   ├── controller/  # REST controllers
│   │   │   ├── dto/         # Immutable Java records & DTOs
│   │   │   ├── engine/      # Strategy Pattern rule engine & rules/
│   │   │   ├── exception/   # GlobalExceptionHandler
│   │   │   ├── model/       # JPA entities
│   │   │   ├── repository/  # Spring Data JPA repositories & Specifications
│   │   │   └── service/     # Core business services
│   │   └── resources/
│   │       ├── application.yml
│   │       └── db/migration/# Flyway SQL scripts (V1 - V5)
│   └── test/                # JUnit 5 & MockMvc unit/integration tests
├── Dockerfile               # Backend multi-stage build
├── docker-compose.yml       # Production stack composition
└── pom.xml                  # Maven build definition & JaCoCo plugin
```

---

## 🛠️ Coding Conventions

1. **Java 21**: Utilize Java 21 features (records for DTOs, text blocks, pattern matching, switch expressions).
2. **Strategy Pattern for Rules**: Extend `FraudRule` strategy interface when introducing new fraud detection rules.
3. **Immutability**: Ensure entities like `FraudCaseAudit` are write-once without setters.
4. **React & TypeScript**: Functional components only, strict mode enabled, no inline styles, use Tailwind utility classes.
5. **Data Fetching**: Use TanStack React Query hooks; avoid `useEffect` for API fetching.

---

## 🧪 Testing Workflows

### Run Backend Test Suite
```bash
mvn clean verify
# JaCoCo Report: target/site/jacoco/index.html
```

### Run Frontend Test Suite
```bash
cd frontend
npm test
```
