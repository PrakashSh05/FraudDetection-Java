package com.fraudapi.engine;

import com.fraudapi.constants.RuleSeverity;
import com.fraudapi.dto.TriggeredRule;

import java.util.Optional;

/**
 * Strategy interface representing a single, independent fraud rule evaluation.
 * Implementations evaluate exactly one risk indicator without knowledge of other rules or scoring logic.
 */
public interface FraudRule {

    /**
     * Unique identifier for the rule (e.g. "RULE-001").
     *
     * @return the unique rule ID
     */
    String getRuleId();

    /**
     * Human-readable code or name of the rule (e.g. "HIGH_AMOUNT").
     *
     * @return the rule name
     */
    String getRuleName();

    /**
     * Category of the rule (e.g. "TRANSACTION", "VELOCITY").
     *
     * @return the rule category
     */
    String getCategory();

    /**
     * Severity classification of the rule.
     *
     * @return the rule severity
     */
    RuleSeverity getSeverity();

    /**
     * Risk points contributed by this rule when triggered.
     *
     * @return the point weight
     */
    int getWeight();

    /**
     * Evaluates the transaction context against this rule's specific risk criteria.
     *
     * @param context the transaction context holding evaluation data
     * @return an {@link Optional} containing {@link TriggeredRule} telemetry if triggered, or empty if clean
     */
    Optional<TriggeredRule> evaluate(TransactionContext context);
}
