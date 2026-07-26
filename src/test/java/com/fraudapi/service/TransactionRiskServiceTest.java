package com.fraudapi.service;

import com.fraudapi.constants.Decision;
import com.fraudapi.constants.RiskLevel;
import com.fraudapi.constants.RuleSeverity;
import com.fraudapi.dto.FraudDecision;
import com.fraudapi.dto.TriggeredRule;
import com.fraudapi.engine.DecisionEngine;
import com.fraudapi.engine.FraudRule;
import com.fraudapi.engine.TransactionContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link TransactionRiskService}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TransactionRiskService Unit Tests")
class TransactionRiskServiceTest {

    @Mock
    private FraudRule rule1;

    @Mock
    private FraudRule rule2;

    private DecisionEngine decisionEngine;
    private TransactionRiskService riskService;

    @BeforeEach
    void setUp() {
        decisionEngine = new DecisionEngine();
        riskService = new TransactionRiskService(List.of(rule1, rule2), decisionEngine);
    }

    @Test
    @DisplayName("Clean transaction with zero triggered rules -> Score 0, LOW, APPROVED")
    void testCleanTransaction() {
        when(rule1.evaluate(any())).thenReturn(Optional.empty());
        when(rule2.evaluate(any())).thenReturn(Optional.empty());

        TransactionContext context = TransactionContext.builder()
                .userId(1L)
                .amount(new BigDecimal("5000.00"))
                .build();

        FraudDecision decision = riskService.evaluateTransactionRisk(context);

        assertNotNull(decision);
        assertEquals(0, decision.getRiskScore());
        assertEquals(RiskLevel.LOW, decision.getRiskLevel());
        assertEquals(Decision.APPROVED, decision.getDecision());
        assertEquals("No fraud indicators detected.", decision.getSummary());
        assertTrue(decision.getTriggeredRules().isEmpty());
    }

    @Test
    @DisplayName("Triggered rule accumulates risk score and triggers decision tier")
    void testTriggeredRule() {
        TriggeredRule triggered = TriggeredRule.builder()
                .ruleId("RULE-001")
                .ruleName("HIGH_AMOUNT")
                .category("TRANSACTION")
                .severity(RuleSeverity.HIGH)
                .points(35)
                .description("Transaction amount exceeded threshold")
                .build();

        when(rule1.evaluate(any())).thenReturn(Optional.of(triggered));
        when(rule2.evaluate(any())).thenReturn(Optional.empty());

        TransactionContext context = TransactionContext.builder()
                .userId(1L)
                .amount(new BigDecimal("75000.00"))
                .build();

        FraudDecision decision = riskService.evaluateTransactionRisk(context);

        assertNotNull(decision);
        assertEquals(35, decision.getRiskScore());
        assertEquals(RiskLevel.MEDIUM, decision.getRiskLevel());
        assertEquals(Decision.MONITOR, decision.getDecision());
        assertEquals(1, decision.getTriggeredRules().size());
    }

    @Test
    @DisplayName("Failing rule exception should be handled gracefully without stopping evaluation")
    void testFailingRuleException_ShouldContinueEvaluation() {
        TriggeredRule triggered = TriggeredRule.builder()
                .ruleId("RULE-002")
                .ruleName("VELOCITY_EXCEEDED")
                .category("VELOCITY")
                .severity(RuleSeverity.MEDIUM)
                .points(25)
                .description("Velocity limit exceeded")
                .build();

        when(rule1.evaluate(any())).thenThrow(new RuntimeException("Database timeout"));
        when(rule2.evaluate(any())).thenReturn(Optional.of(triggered));

        TransactionContext context = TransactionContext.builder()
                .userId(1L)
                .amount(new BigDecimal("2000.00"))
                .build();

        FraudDecision decision = riskService.evaluateTransactionRisk(context);

        assertNotNull(decision);
        assertEquals(25, decision.getRiskScore());
        assertEquals(RiskLevel.LOW, decision.getRiskLevel());
        assertEquals(Decision.APPROVED, decision.getDecision());
        assertEquals(1, decision.getTriggeredRules().size());
    }
}
