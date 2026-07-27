# System Architecture & Technical Specifications 🏗️

The **Transaction Risk Analysis Platform V2** is designed around a clean, layered, domain-driven architecture following the **Open/Closed Principle (OCP)** and **Strategy Pattern**.

---

## 1. Overall System Architecture

```mermaid
graph TD
    Client[Web Browser / React SPA] -->|HTTP / REST API| Nginx[Nginx Reverse Proxy :3000]
    Nginx -->|Proxy /api| SpringBoot[Spring Boot Backend :8080]
    
    subgraph Spring Boot Application
        ControllerLayer[REST Controllers]
        ServiceLayer[Business Services]
        EngineLayer[Strategy Pattern Rule Engine]
        DecisionLayer[Decision Engine]
        RepoLayer[JPA Repositories]
    end

    SpringBoot --> ControllerLayer
    ControllerLayer --> ServiceLayer
    ServiceLayer --> EngineLayer
    EngineLayer --> DecisionLayer
    ServiceLayer --> RepoLayer
    RepoLayer --> Database[(Relational Database / H2 / MySQL)]
```

---

## 2. Fraud Evaluation Engine Flow

The rule engine evaluates transactions dynamically using registered `FraudRule` strategies without modifying the central orchestration layer.

```mermaid
sequenceDiagram
    autonumber
    participant Client as Client API / UI
    participant TxnService as TransactionService
    participant RiskService as TransactionRiskService
    participant RuleEngine as FraudRule Strategies
    participant DecisionEngine as DecisionEngine
    participant DB as Database

    Client->>TxnService: POST /api/transactions
    TxnService->>RiskService: evaluateTransactionRisk(context)
    RiskService->>RuleEngine: evaluate(context) [HighAmountRule, VelocityRule, RoundAmountRule, RepeatedAmountRule]
    RuleEngine-->>RiskService: List<TriggeredRule>
    RiskService->>DecisionEngine: evaluateDecision(totalScore, rules, latency)
    DecisionEngine-->>RiskService: FraudDecision (score capped at 100)
    RiskService-->>TxnService: FraudDecision
    
    alt Decision == REVIEW
        TxnService->>DB: Save Transaction (FLAGGED) + Risk Events + FraudCase (OPEN)
    else Decision == REJECTED
        TxnService->>DB: Save Transaction (FLAGGED) + Risk Events
    else Decision == APPROVED / MONITOR
        TxnService->>DB: Save Transaction (APPROVED) + Deduct Balance
    end

    TxnService-->>Client: TransactionResponse
```

---

## 3. Fraud Case Management State Machine

```mermaid
stateDiagram-v2
    [*] --> OPEN: Transaction evaluated as REVIEW
    OPEN --> ASSIGNED: Assigned to analyst
    OPEN --> UNDER_REVIEW: Review initiated
    ASSIGNED --> UNDER_REVIEW: Analyst begins investigation
    UNDER_REVIEW --> APPROVED: Transaction cleared
    UNDER_REVIEW --> DECLINED: Transaction confirmed fraudulent
    UNDER_REVIEW --> ESCALATED: Escalated to senior compliance
    
    APPROVED --> CLOSED: Case finalized & closed
    DECLINED --> CLOSED: Case finalized & closed
    ESCALATED --> CLOSED: Case finalized & closed
    CLOSED --> [*]: Immutable state (No edits allowed)
```

---

## 4. Database Entity-Relationship Diagram (ERD)

```mermaid
erDiagram
    USERS ||--o{ TRANSACTIONS : "initiates"
    TRANSACTIONS ||--o{ TRANSACTION_RISK_EVENTS : "triggers"
    TRANSACTIONS ||--o| FRAUD_CASES : "opens (1-to-1)"
    FRAUD_CASES ||--o{ FRAUD_CASE_AUDITS : "records timeline"

    USERS {
        bigint id PK
        string name
        string email
        decimal balance
        timestamp created_at
    }

    TRANSACTIONS {
        bigint id PK
        bigint user_id FK
        decimal amount
        string transaction_type
        string status
        integer risk_score
        string risk_level
        string decision
        long processing_time_ms
        timestamp evaluation_timestamp
        timestamp created_at
    }

    TRANSACTION_RISK_EVENTS {
        bigint id PK
        bigint transaction_id FK
        string rule_id
        string rule_name
        string category
        string severity
        integer points
        string description
        timestamp created_at
    }

    FRAUD_CASES {
        bigint id PK
        bigint transaction_id FK
        string status
        string priority
        string assigned_to
        timestamp opened_at
        timestamp closed_at
        string resolution
        string review_notes
        timestamp created_at
    }

    FRAUD_CASE_AUDITS {
        bigint id PK
        bigint fraud_case_id FK
        string event_type
        string old_value
        string new_value
        string performed_by
        timestamp timestamp
    }
```

---

## 5. Container Deployment Architecture

```mermaid
graph LR
    subgraph Host Environment
        Subnet[Docker Bridge Network: risk-network]
        
        subgraph Frontend Container
            Nginx[Nginx Web Server :80]
            SPA[React Production Build]
        end
        
        subgraph Backend Container
            App[Java 21 JRE Runtime :8080]
            Flyway[Flyway Migration Engine]
        end
        
        Nginx -->|Port 3000| Client[User Browser]
        Nginx -->|/api Proxy| App
        App --> Flyway
    end
```
