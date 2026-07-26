package com.fraudapi.dto.analytics;

/**
 * Aggregated trigger count metric for a specific fraud rule.
 */
public record TopRuleResponse(
        String ruleId,
        String ruleName,
        long triggerCount
) {}
