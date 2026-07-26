-- Flyway Migration V4: Create fraud_cases table for manual review workflows
CREATE TABLE IF NOT EXISTS fraud_cases (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_id BIGINT NOT NULL UNIQUE,
    status VARCHAR(30) NOT NULL,
    priority VARCHAR(20) NOT NULL,
    assigned_to VARCHAR(100),
    opened_at TIMESTAMP NOT NULL,
    closed_at TIMESTAMP,
    resolution VARCHAR(255),
    review_notes VARCHAR(1000),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_fraud_case_txn FOREIGN KEY (transaction_id) REFERENCES transactions(id) ON DELETE CASCADE
);

CREATE INDEX idx_fraud_case_status ON fraud_cases(status);
CREATE INDEX idx_fraud_case_priority ON fraud_cases(priority);
