package com.fraudapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Value;

/**
 * Immutable Data Transfer Object representing a specific fraud rule that fired during evaluation.
 */
@Getter
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class TriggeredRule {

    /** Unique identifier for the rule (e.g. "RULE-001"). */
    private final String ruleName;

    /** Human-readable code or name of the rule (e.g. "HIGH_AMOUNT"). */
    private final String ruleId;

    /** Category of the rule (e.g. "TRANSACTION", "VELOCITY"). */
    private final String category;

    /** Severity level of the triggered rule (e.g. "LOW", "MEDIUM", "HIGH", "CRITICAL"). */
    private final String severity;

    /** Risk points contributed by this rule towards the total score. */
    private final int points;

    /** Explanation of why the rule fired. */
    private final String description;
}
