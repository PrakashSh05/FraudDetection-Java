package com.fraudapi.dto;

import com.fraudapi.constants.RuleSeverity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Immutable Data Transfer Object representing a specific fraud rule that fired during evaluation.
 */
@Getter
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class TriggeredRule {

    /** Unique identifier for the rule (e.g. "RULE-001"). */
    private final String ruleId;

    /** Human-readable code or name of the rule (e.g. "HIGH_AMOUNT"). */
    private final String ruleName;

    /** Category of the rule (e.g. "TRANSACTION", "VELOCITY"). */
    private final String category;

    /** Severity classification of the triggered rule. */
    private final RuleSeverity severity;

    /** Risk points contributed by this rule towards the total score. */
    private final int points;

    /** Explanation of why the rule fired. */
    private final String description;
}
