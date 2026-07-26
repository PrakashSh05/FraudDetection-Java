package com.fraudapi.engine.rules;

import com.fraudapi.config.FraudRuleProperties;
import com.fraudapi.constants.RuleSeverity;
import com.fraudapi.dto.TriggeredRule;
import com.fraudapi.engine.FraudRule;
import com.fraudapi.engine.TransactionContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Fraud rule evaluating if a single transaction amount exceeds the configured threshold.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class HighAmountRule implements FraudRule {

    public static final String RULE_ID = "RULE-001";
    public static final String RULE_NAME = "HIGH_AMOUNT";
    public static final String CATEGORY = "TRANSACTION";
    public static final RuleSeverity SEVERITY = RuleSeverity.HIGH;

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
        return properties.getHighAmount().getPoints();
    }

    @Override
    public Optional<TriggeredRule> evaluate(TransactionContext context) {
        if (context == null || context.getAmount() == null) {
            return Optional.empty();
        }

        double threshold = properties.getHighAmount().getThreshold();
        BigDecimal thresholdBd = BigDecimal.valueOf(threshold);

        if (context.getAmount().compareTo(thresholdBd) > 0) {
            log.warn("Fraud rule TRIGGERED [{}] userId={} amount={} threshold={}",
                    RULE_ID, context.getUserId(), context.getAmount(), threshold);

            String description = String.format(
                    "Transaction amount exceeded configured threshold. Actual: %.2f, Threshold: %.2f",
                    context.getAmount().doubleValue(), threshold);

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
