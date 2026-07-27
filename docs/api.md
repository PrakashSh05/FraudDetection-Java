# REST API Documentation 📖

Base URL: `http://localhost:8080/api`

---

## 1. Standard Response Envelopes

### Success Envelope
```json
{
  "status": "success",
  "message": "Operation completed successfully",
  "data": { ... },
  "timestamp": "2026-07-26T20:30:00"
}
```

### Error Envelope
```json
{
  "status": "error",
  "message": "Detailed error message",
  "data": null,
  "timestamp": "2026-07-26T20:30:00"
}
```

---

## 2. Endpoints Reference

### 💳 Transactions

#### `POST /api/transactions`
Creates a transaction and executes real-time fraud risk evaluation.

**Request Body:**
```json
{
  "userId": 1,
  "amount": 75000.00,
  "transactionType": "DEBIT"
}
```

**Response (200 OK):**
```json
{
  "status": "success",
  "message": "Transaction created successfully",
  "data": {
    "id": 101,
    "userId": 1,
    "amount": 75000.00,
    "transactionType": "DEBIT",
    "status": "FLAGGED",
    "fraudReason": "Transaction amount exceeded configured threshold. Actual: 75000.00, Threshold: 50000.00",
    "newBalance": null,
    "createdAt": "2026-07-26T20:30:00"
  }
}
```

---

### 📊 Analytics

#### `GET /api/analytics/overview`
Returns aggregate platform metrics.

#### `GET /api/analytics/risk-distribution`
Returns count breakdown by risk level (`LOW`, `MEDIUM`, `HIGH`, `CRITICAL`).

#### `GET /api/analytics/daily-trend`
Returns daily transaction volumes and risk metrics.

#### `GET /api/analytics/top-rules`
Returns top fired fraud rules.

---

### 🔍 Investigation Audit

#### `GET /api/investigation/transaction/{transactionId}`
Reconstructs complete risk evaluation, score breakdown, and triggered rules for a transaction.

---

### 📁 Fraud Case Management

#### `GET /api/cases`
Returns paginated fraud case queue. Supports dynamic filtering by `status`, `priority`, `assignedTo`, `riskLevel`, `transactionId`, `caseId`, `page`, `size`, `sort`.

#### `GET /api/cases/summary`
Returns queue count statistics grouped by status and priority.

#### `GET /api/cases/{caseId}`
Returns detailed information for a single fraud case.

#### `GET /api/cases/{caseId}/timeline`
Returns chronological write-once audit log entries.

#### `PATCH /api/cases/{caseId}/assign`
Assigns case to an analyst.

#### `PATCH /api/cases/{caseId}/status`
Transitions case workflow state.

#### `PATCH /api/cases/{caseId}/notes`
Appends/updates review notes.

#### `PATCH /api/cases/{caseId}/resolve`
Resolves and finalizes a case (`closedAt` timestamp set).
