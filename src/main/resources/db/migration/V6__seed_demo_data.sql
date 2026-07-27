-- Flyway Migration V6: Seed realistic demonstration data for platform V1.0 presentation

-- 1. Seed Initial Users
INSERT INTO users (id, name, email, balance, created_at) VALUES
(1, 'Rahul Sharma', 'rahul.sharma@example.com', 250000.00, '2026-07-01 10:00:00'),
(2, 'Ananya Verma', 'ananya.verma@example.com', 180000.00, '2026-07-02 11:30:00'),
(3, 'Vikram Patel', 'vikram.patel@example.com', 450000.00, '2026-07-03 09:15:00'),
(4, 'Priya Nair', 'priya.nair@example.com', 95000.00, '2026-07-04 14:20:00'),
(5, 'Amit Kumar', 'amit.kumar@example.com', 320000.00, '2026-07-05 16:45:00')
ON DUPLICATE KEY UPDATE id=id;

-- 2. Seed Approved & Flagged Historical Transactions
INSERT INTO transactions (id, user_id, amount, transaction_type, status, risk_score, risk_level, decision, processing_time_ms, evaluation_timestamp, fraud_reason, created_at) VALUES
(101, 1, 2500.00, 'DEBIT', 'APPROVED', 0, 'LOW', 'APPROVED', 2, '2026-07-20 09:00:00', NULL, '2026-07-20 09:00:00'),
(102, 1, 1500.00, 'DEBIT', 'APPROVED', 0, 'LOW', 'APPROVED', 2, '2026-07-20 10:30:00', NULL, '2026-07-20 10:30:00'),
(103, 1, 75000.00, 'DEBIT', 'FLAGGED', 35, 'MEDIUM', 'REJECTED', 5, '2026-07-21 11:15:00', 'Transaction amount exceeded configured threshold. Actual: 75000.00, Threshold: 50000.00', '2026-07-21 11:15:00'),
(104, 2, 85000.00, 'DEBIT', 'FLAGGED', 65, 'HIGH', 'REVIEW', 6, '2026-07-22 14:00:00', 'Transaction amount exceeded configured threshold. Actual: 85000.00, Threshold: 50000.00', '2026-07-22 14:00:00'),
(105, 3, 120000.00, 'DEBIT', 'FLAGGED', 85, 'CRITICAL', 'REJECTED', 7, '2026-07-23 16:30:00', 'Transaction amount exceeded configured threshold. Actual: 120000.00, Threshold: 50000.00', '2026-07-23 16:30:00'),
(106, 4, 60000.00, 'DEBIT', 'FLAGGED', 60, 'HIGH', 'REVIEW', 5, '2026-07-24 10:00:00', 'Transaction amount exceeded configured threshold. Actual: 60000.00, Threshold: 50000.00', '2026-07-24 10:00:00'),
(107, 5, 95000.00, 'DEBIT', 'FLAGGED', 65, 'HIGH', 'REVIEW', 6, '2026-07-25 15:20:00', 'Transaction amount exceeded configured threshold. Actual: 95000.00, Threshold: 50000.00', '2026-07-25 15:20:00'),
(108, 2, 4500.00, 'DEBIT', 'APPROVED', 0, 'LOW', 'APPROVED', 3, '2026-07-26 08:30:00', NULL, '2026-07-26 08:30:00'),
(109, 3, 70000.00, 'DEBIT', 'FLAGGED', 65, 'HIGH', 'REVIEW', 5, '2026-07-26 12:45:00', 'Transaction amount exceeded configured threshold. Actual: 70000.00, Threshold: 50000.00', '2026-07-26 12:45:00')
ON DUPLICATE KEY UPDATE id=id;

-- 3. Seed Triggered Risk Events
INSERT INTO transaction_risk_events (id, transaction_id, rule_id, rule_name, category, severity, points, description, created_at) VALUES
(1, 103, 'RULE-001', 'HIGH_AMOUNT', 'TRANSACTION', 'HIGH', 35, 'Transaction amount exceeded configured threshold. Actual: 75000.00, Threshold: 50000.00', '2026-07-21 11:15:00'),
(2, 104, 'RULE-001', 'HIGH_AMOUNT', 'TRANSACTION', 'HIGH', 65, 'Transaction amount exceeded configured threshold. Actual: 85000.00, Threshold: 50000.00', '2026-07-22 14:00:00'),
(3, 105, 'RULE-001', 'HIGH_AMOUNT', 'TRANSACTION', 'HIGH', 35, 'Transaction amount exceeded configured threshold. Actual: 120000.00, Threshold: 50000.00', '2026-07-23 16:30:00'),
(4, 105, 'RULE-002', 'VELOCITY_EXCEEDED', 'VELOCITY', 'MEDIUM', 50, 'Transaction velocity exceeded configured limit. Actual: 4 in 5 mins, Limit: 3', '2026-07-23 16:30:00'),
(5, 106, 'RULE-001', 'HIGH_AMOUNT', 'TRANSACTION', 'HIGH', 60, 'Transaction amount exceeded configured threshold. Actual: 60000.00, Threshold: 50000.00', '2026-07-24 10:00:00'),
(6, 107, 'RULE-001', 'HIGH_AMOUNT', 'TRANSACTION', 'HIGH', 65, 'Transaction amount exceeded configured threshold. Actual: 95000.00, Threshold: 50000.00', '2026-07-25 15:20:00'),
(7, 109, 'RULE-001', 'HIGH_AMOUNT', 'TRANSACTION', 'HIGH', 65, 'Transaction amount exceeded configured threshold. Actual: 70000.00, Threshold: 50000.00', '2026-07-26 12:45:00')
ON DUPLICATE KEY UPDATE id=id;

-- 4. Seed Fraud Cases Across Lifecycle Stages
INSERT INTO fraud_cases (id, transaction_id, status, priority, assigned_to, opened_at, closed_at, resolution, review_notes, created_at, updated_at) VALUES
(1, 104, 'UNDER_REVIEW', 'HIGH', 'john.doe', '2026-07-22 14:00:00', NULL, NULL, 'Initial review started by compliance team.', '2026-07-22 14:00:00', '2026-07-22 14:15:00'),
(2, 106, 'ASSIGNED', 'HIGH', 'alice.smith', '2026-07-24 10:00:00', NULL, NULL, NULL, '2026-07-24 10:00:00', '2026-07-24 10:30:00'),
(3, 107, 'CLOSED', 'HIGH', 'john.doe', '2026-07-25 15:20:00', '2026-07-25 16:00:00', 'Large transfer confirmed by customer over phone verification.', 'Customer identity verified cleanly.', '2026-07-25 15:20:00', '2026-07-25 16:00:00'),
(4, 109, 'OPEN', 'HIGH', NULL, '2026-07-26 12:45:00', NULL, NULL, NULL, '2026-07-26 12:45:00', '2026-07-26 12:45:00')
ON DUPLICATE KEY UPDATE id=id;

-- 5. Seed Audit Timeline Records
INSERT INTO fraud_case_audits (id, fraud_case_id, event_type, old_value, new_value, performed_by, timestamp) VALUES
(1, 1, 'CASE_CREATED', NULL, 'OPEN', 'SYSTEM', '2026-07-22 14:00:00'),
(2, 1, 'CASE_ASSIGNED', NULL, 'john.doe', 'john.doe', '2026-07-22 14:05:00'),
(3, 1, 'STATUS_CHANGED', 'ASSIGNED', 'UNDER_REVIEW', 'john.doe', '2026-07-22 14:15:00'),
(4, 2, 'CASE_CREATED', NULL, 'OPEN', 'SYSTEM', '2026-07-24 10:00:00'),
(5, 2, 'CASE_ASSIGNED', NULL, 'alice.smith', 'alice.smith', '2026-07-24 10:30:00'),
(6, 3, 'CASE_CREATED', NULL, 'OPEN', 'SYSTEM', '2026-07-25 15:20:00'),
(7, 3, 'CASE_ASSIGNED', NULL, 'john.doe', 'john.doe', '2026-07-25 15:25:00'),
(8, 3, 'CASE_RESOLVED', 'UNDER_REVIEW', 'APPROVED', 'john.doe', '2026-07-25 16:00:00'),
(9, 3, 'CASE_CLOSED', NULL, 'APPROVED', 'john.doe', '2026-07-25 16:00:00'),
(10, 4, 'CASE_CREATED', NULL, 'OPEN', 'SYSTEM', '2026-07-26 12:45:00')
ON DUPLICATE KEY UPDATE id=id;
