package com.fraudapi.dto.cases;

import com.fraudapi.constants.FraudCaseAuditEventType;

import java.time.LocalDateTime;

/**
 * DTO record representing an individual audit timeline entry for a fraud case.
 */
public record FraudCaseAuditResponse(
        Long id,
        FraudCaseAuditEventType eventType,
        String oldValue,
        String newValue,
        String performedBy,
        LocalDateTime timestamp
) {}
