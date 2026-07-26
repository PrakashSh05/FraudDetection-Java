package com.fraudapi.dto.cases;

import com.fraudapi.constants.FraudCasePriority;
import com.fraudapi.constants.FraudCaseStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Summary record response for paginated fraud case queues.
 */
public record FraudCaseSummaryResponse(
        Long caseId,
        Long transactionId,
        Long userId,
        BigDecimal amount,
        String transactionType,
        Integer riskScore,
        String riskLevel,
        FraudCaseStatus status,
        FraudCasePriority priority,
        String assignedTo,
        LocalDateTime openedAt,
        LocalDateTime createdAt
) {}
