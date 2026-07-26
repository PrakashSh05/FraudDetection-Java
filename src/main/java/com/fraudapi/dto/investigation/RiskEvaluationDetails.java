package com.fraudapi.dto.investigation;

import java.time.LocalDateTime;

/**
 * Persisted risk telemetry and decision details for fraud investigation reports.
 */
public record RiskEvaluationDetails(
        Integer riskScore,
        String riskLevel,
        String decision,
        Long processingTimeMs,
        LocalDateTime evaluationTimestamp
) {}
