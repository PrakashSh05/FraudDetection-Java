-- Flyway Migration V2: Add Risk Evaluation Telemetry Columns to Transactions
ALTER TABLE transactions ADD COLUMN IF NOT EXISTS risk_score INT;
ALTER TABLE transactions ADD COLUMN IF NOT EXISTS risk_level VARCHAR(20);
ALTER TABLE transactions ADD COLUMN IF NOT EXISTS decision VARCHAR(20);
ALTER TABLE transactions ADD COLUMN IF NOT EXISTS processing_time_ms BIGINT;
ALTER TABLE transactions ADD COLUMN IF NOT EXISTS evaluation_timestamp TIMESTAMP;
