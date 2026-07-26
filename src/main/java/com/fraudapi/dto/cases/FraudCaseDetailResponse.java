package com.fraudapi.dto.cases;

import com.fraudapi.constants.FraudCasePriority;
import com.fraudapi.constants.FraudCaseStatus;
import com.fraudapi.dto.investigation.RiskEvaluationDetails;
import com.fraudapi.dto.investigation.TransactionDetails;
import com.fraudapi.dto.investigation.TriggeredRuleDetails;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Detailed investigation payload for a single fraud case.
 */
public record FraudCaseDetailResponse(
        Long caseId,
        FraudCaseStatus status,
        FraudCasePriority priority,
        String assignedTo,
        LocalDateTime openedAt,
        LocalDateTime closedAt,
        String resolution,
        String reviewNotes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        TransactionDetails transaction,
        RiskEvaluationDetails evaluation,
        List<TriggeredRuleDetails> triggeredRules
) {}
