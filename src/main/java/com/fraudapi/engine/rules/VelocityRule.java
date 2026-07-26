package com.fraudapi.engine.rules;

import com.fraudapi.config.FraudRuleProperties;
import com.fraudapi.constants.RuleSeverity;
import com.fraudapi.dto.TriggeredRule;
import com.fraudapi.engine.FraudRule;
import com.fraudapi.engine.TransactionContext;
import com.fraudapi.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Fraud rule evaluating transaction frequency within a rolling time window (velocity check).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class VelocityRule implements FraudRule {

    public static final String RULE_ID = "RULE-002";
    public static final String RULE_NAME = "VELOCITY_EXCEEDED";
    public static final String CATEGORY = "VELOCITY";
    public static final RuleSeverity SEVERITY = RuleSeverity.MEDIUM;

    private final TransactionRepository transactionRepository;
    private final FraudRuleProperties properties;

    @Override
    public String getRuleId() {
        return RULE_ID;
    }

    @Override
    public String getRuleName() {
        return RULE_NAME;
    }

    @Override
    public String getCategory() {
        return CATEGORY;
    }

    @Override
    public RuleSeverity getSeverity() {
        return SEVERITY;
    }

    @Override
    public int getWeight() {
        return properties.getVelocity().getPoints();
    }

    @Override
    public Optional<TriggeredRule> evaluate(TransactionContext context) {
        if (context == null || context.getUserId() == null) {
            return Optional.empty();
        }

        int windowMinutes = properties.getVelocity().getWindowMinutes();
        int maxTransactions = properties.getVelocity().getMaxTransactions();

        LocalDateTime windowStart = LocalDateTime.now().minusMinutes(windowMinutes);
        long recentCount = transactionRepository.countRecentTransactions(context.getUserId(), windowStart);

        if (recentCount >= maxTransactions) {
            log.warn("Fraud rule TRIGGERED [{}] userId={} recentCount={} maxAllowed={}",
                    RULE_ID, context.getUserId(), recentCount, maxTransactions);

            String description = String.format(
                    "Transaction velocity exceeded configured limit. Actual: %d in %d mins, Limit: %d",
                    recentCount, windowMinutes, maxTransactions);

            TriggeredRule triggered = TriggeredRule.builder()
                    .ruleId(RULE_ID)
                    .ruleName(RULE_NAME)
                    .category(CATEGORY)
                    .severity(SEVERITY)
                    .points(getWeight())
                    .description(description)
                    .build();

            return Optional.of(triggered);
        }

        return Optional.empty();
    }
}
