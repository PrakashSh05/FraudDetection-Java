-- Flyway Migration V3: Create transaction_risk_events table for rule telemetry
CREATE TABLE IF NOT EXISTS transaction_risk_events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_id BIGINT NOT NULL,
    rule_id VARCHAR(50) NOT NULL,
    rule_name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    points INT NOT NULL,
    description VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_risk_event_txn FOREIGN KEY (transaction_id) REFERENCES transactions(id) ON DELETE CASCADE
);

CREATE INDEX idx_risk_event_txn_id ON transaction_risk_events(transaction_id);
