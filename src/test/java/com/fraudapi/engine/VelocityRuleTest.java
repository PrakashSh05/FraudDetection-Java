package com.fraudapi.engine;

import com.fraudapi.config.FraudRuleProperties;
import com.fraudapi.constants.RuleSeverity;
import com.fraudapi.dto.TriggeredRule;
import com.fraudapi.engine.rules.VelocityRule;
import com.fraudapi.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Comprehensive unit tests for {@link VelocityRule}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("VelocityRule Unit Tests")
class VelocityRuleTest {

    @Mock
    private TransactionRepository transactionRepository;

    private VelocityRule velocityRule;

    @BeforeEach
    void setUp() {
        FraudRuleProperties properties = new FraudRuleProperties();
        properties.getVelocity().setMaxTransactions(3);
        properties.getVelocity().setWindowMinutes(5);
        properties.getVelocity().setPoints(25);

        velocityRule = new VelocityRule(transactionRepository, properties);
    }

    @Test
    @DisplayName("Transaction count in 5 min window >= 3 -> triggers velocity rule")
    void testEvaluate_VelocityExceeded_TriggersRule() {
        TransactionContext context = TransactionContext.builder()
                .userId(1L)
                .amount(new BigDecimal("1000.00"))
                .build();

        // VelocityRule calls countRecentTransactions (not findByUserIdAndCreatedAtAfter)
        when(transactionRepository.countRecentTransactions(eq(1L), any()))
                .thenReturn(3L);

        Optional<TriggeredRule> result = velocityRule.evaluate(context);

        assertTrue(result.isPresent());
        TriggeredRule rule = result.get();
        assertEquals("RULE-002", rule.getRuleId());
        assertEquals("VELOCITY_EXCEEDED", rule.getRuleName());
        assertEquals(RuleSeverity.MEDIUM, rule.getSeverity());
        assertEquals(25, rule.getPoints());
        assertTrue(rule.getDescription().contains("velocity"));
    }

    @Test
    @DisplayName("Transaction count < 3 limit -> does NOT trigger velocity rule")
    void testEvaluate_VelocityUnderLimit_DoesNotTrigger() {
        TransactionContext context = TransactionContext.builder()
                .userId(1L)
                .amount(new BigDecimal("1000.00"))
                .build();

        // VelocityRule calls countRecentTransactions — returns 1 (under limit of 3)
        when(transactionRepository.countRecentTransactions(eq(1L), any()))
                .thenReturn(1L);

        Optional<TriggeredRule> result = velocityRule.evaluate(context);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Null userId -> returns empty without error")
    void testEvaluate_NullUserId_DoesNotTrigger() {
        TransactionContext context = TransactionContext.builder().userId(null).build();
        assertTrue(velocityRule.evaluate(context).isEmpty());
    }
}
