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
 * Fraud rule detecting suspiciously round transaction amounts.
 *
 * <p>Fraudsters commonly use perfectly round amounts (e.g. ₹50,000, ₹1,00,000)
 * to avoid automated detection thresholds. This rule flags large round amounts
 * as a supplementary fraud signal.
 *
 * <p>Rule fires when: amount > minimumAmount AND (amount % roundingDivisor == 0)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RoundAmountRule implements FraudRule {

    public static final String RULE_ID   = "RULE-003";
    public static final String RULE_NAME = "ROUND_AMOUNT";
    public static final String CATEGORY  = "PATTERN";
    public static final RuleSeverity SEVERITY = RuleSeverity.MEDIUM;

    private final FraudRuleProperties properties;

    @Override
    public String getRuleId() { return RULE_ID; }

    @Override
    public String getRuleName() { return RULE_NAME; }

    @Override
    public String getCategory() { return CATEGORY; }

    @Override
    public RuleSeverity getSeverity() { return SEVERITY; }

    @Override
    public int getWeight() { return properties.getRoundAmount().getPoints(); }

    @Override
    public Optional<TriggeredRule> evaluate(TransactionContext context) {
        if (context == null || context.getAmount() == null) {
            return Optional.empty();
        }

        double minimumAmount = properties.getRoundAmount().getMinimumAmount();
        int divisor = properties.getRoundAmount().getRoundingDivisor();
        BigDecimal amount = context.getAmount();

        // Only check amounts above the minimum threshold
        if (amount.compareTo(BigDecimal.valueOf(minimumAmount)) <= 0) {
            return Optional.empty();
        }

        // Check if the amount is a round number (no remainder when divided by divisor)
        boolean isRound = amount.remainder(BigDecimal.valueOf(divisor))
                .compareTo(BigDecimal.ZERO) == 0;

        if (isRound) {
            log.warn("Fraud rule TRIGGERED [{}] userId={} amount={} (round number, divisible by {})",
                    RULE_ID, context.getUserId(), amount, divisor);

            String description = String.format(
                    "Suspicious round transaction amount detected: %.2f is divisible by %d. " +
                    "Round amounts are a common structuring indicator.",
                    amount.doubleValue(), divisor);

            return Optional.of(TriggeredRule.builder()
                    .ruleId(RULE_ID)
                    .ruleName(RULE_NAME)
                    .category(CATEGORY)
                    .severity(SEVERITY)
                    .points(getWeight())
                    .description(description)
                    .build());
        }

        return Optional.empty();
    }
}
