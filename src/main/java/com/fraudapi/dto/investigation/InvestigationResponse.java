package com.fraudapi.dto.investigation;

import java.util.List;

/**
 * Top-level response container reconstructing the full fraud investigation audit trail for a transaction.
 */
public record InvestigationResponse(
        TransactionDetails transaction,
        RiskEvaluationDetails evaluation,
        List<TriggeredRuleDetails> triggeredRules
) {}
