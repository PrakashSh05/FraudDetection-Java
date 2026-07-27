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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Fraud rule detecting repeated identical transaction amounts within a rolling time window.
 *
 * <p>Structuring (also known as "smurfing") is a common fraud technique where a large
 * amount is split into multiple identical smaller transactions to avoid automated thresholds.
 * This rule triggers when the same user submits 2 or more transactions of the exact same
 * amount within the configured time window.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RepeatedAmountRule implements FraudRule {

    public static final String RULE_ID   = "RULE-004";
    public static final String RULE_NAME = "REPEATED_AMOUNT";
    public static final String CATEGORY  = "PATTERN";
    public static final RuleSeverity SEVERITY = RuleSeverity.HIGH;

    private final TransactionRepository transactionRepository;
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
    public int getWeight() { return properties.getRepeatedAmount().getPoints(); }

    @Override
    public Optional<TriggeredRule> evaluate(TransactionContext context) {
        if (context == null || context.getUserId() == null || context.getAmount() == null) {
            return Optional.empty();
        }

        int windowMinutes = properties.getRepeatedAmount().getWindowMinutes();
        int minRepeatCount = properties.getRepeatedAmount().getMinRepeatCount();

        LocalDateTime windowStart = LocalDateTime.now().minusMinutes(windowMinutes);
        BigDecimal amount = context.getAmount();

        long repeatedCount = transactionRepository.countRepeatedAmountTransactions(
                context.getUserId(), amount, windowStart);

        if (repeatedCount >= minRepeatCount) {
            log.warn("Fraud rule TRIGGERED [{}] userId={} amount={} repeatedCount={} in {} mins",
                    RULE_ID, context.getUserId(), amount, repeatedCount, windowMinutes);

            String description = String.format(
                    "Structuring detected: amount %.2f has been transacted %d time(s) in the last %d minutes. " +
                    "Minimum threshold: %d repetitions.",
                    amount.doubleValue(), repeatedCount, windowMinutes, minRepeatCount);

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
