package com.fraudapi.dto.investigation;

/**
 * Triggered rule details for fraud investigation reports.
 */
public record TriggeredRuleDetails(
        String ruleId,
        String ruleName,
        String category,
        String severity,
        Integer points,
        String description
) {}
