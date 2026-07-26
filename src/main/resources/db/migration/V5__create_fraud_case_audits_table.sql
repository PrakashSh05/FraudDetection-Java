-- Flyway Migration V5: Create fraud_case_audits table for immutable case audit timeline
CREATE TABLE IF NOT EXISTS fraud_case_audits (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fraud_case_id BIGINT NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    old_value VARCHAR(1000),
    new_value VARCHAR(1000),
    performed_by VARCHAR(100) NOT NULL DEFAULT 'SYSTEM',
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_case_id FOREIGN KEY (fraud_case_id) REFERENCES fraud_cases(id) ON DELETE CASCADE
);

CREATE INDEX idx_audit_case_id ON fraud_case_audits(fraud_case_id);
