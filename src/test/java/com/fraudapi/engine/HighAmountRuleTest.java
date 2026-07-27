package com.fraudapi.engine;

import com.fraudapi.config.FraudRuleProperties;
import com.fraudapi.constants.RuleSeverity;
import com.fraudapi.dto.TriggeredRule;
import com.fraudapi.engine.rules.HighAmountRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for {@link HighAmountRule}.
 */
@DisplayName("HighAmountRule Unit Tests")
class HighAmountRuleTest {

    private HighAmountRule highAmountRule;

    @BeforeEach
    void setUp() {
        FraudRuleProperties properties = new FraudRuleProperties();
        // FraudRuleProperties.HighAmount uses double threshold and int points (no severity field)
        properties.getHighAmount().setThreshold(50000.0);
        properties.getHighAmount().setPoints(35);

        highAmountRule = new HighAmountRule(properties);
    }

    @Test
    @DisplayName("Amount > 50000.00 threshold -> triggers rule with 35 risk points")
    void testEvaluate_AboveThreshold_TriggersRule() {
        TransactionContext context = TransactionContext.builder()
                .amount(new BigDecimal("75000.00"))
                .build();

        Optional<TriggeredRule> result = highAmountRule.evaluate(context);

        assertTrue(result.isPresent());
        TriggeredRule rule = result.get();
        assertEquals("RULE-001", rule.getRuleId());
        assertEquals("HIGH_AMOUNT", rule.getRuleName());
        assertEquals(RuleSeverity.HIGH, rule.getSeverity());
        assertEquals(35, rule.getPoints());
        assertTrue(rule.getDescription().contains("75000"));
        assertTrue(rule.getDescription().contains("50000"));
    }

    @Test
    @DisplayName("Amount equal to threshold 50000.00 -> does NOT trigger rule")
    void testEvaluate_ExactThreshold_DoesNotTrigger() {
        TransactionContext context = TransactionContext.builder()
                .amount(new BigDecimal("50000.00"))
                .build();

        Optional<TriggeredRule> result = highAmountRule.evaluate(context);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Amount below threshold -> does NOT trigger rule")
    void testEvaluate_BelowThreshold_DoesNotTrigger() {
        TransactionContext context = TransactionContext.builder()
                .amount(new BigDecimal("1000.00"))
                .build();

        Optional<TriggeredRule> result = highAmountRule.evaluate(context);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Null or missing amount -> returns empty")
    void testEvaluate_NullAmount_DoesNotTrigger() {
        TransactionContext context = TransactionContext.builder().amount(null).build();
        assertTrue(highAmountRule.evaluate(context).isEmpty());
    }
}
