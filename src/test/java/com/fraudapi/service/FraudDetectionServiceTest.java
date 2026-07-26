package com.fraudapi.service;

import com.fraudapi.constants.Decision;
import com.fraudapi.constants.RiskLevel;
import com.fraudapi.constants.RuleSeverity;
import com.fraudapi.dto.FraudDecision;
import com.fraudapi.dto.TriggeredRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Compatibility unit test for legacy {@link FraudDetectionService}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("FraudDetectionService Compatibility Unit Tests")
class FraudDetectionServiceTest {

    @Mock
    private TransactionRiskService transactionRiskService;

    @InjectMocks
    private FraudDetectionService fraudDetectionService;

    @Test
    @DisplayName("Approved decision -> checkFraud returns null")
    void testCleanTransaction_ShouldReturnNull() {
        FraudDecision decision = FraudDecision.builder()
                .riskScore(0)
                .riskLevel(RiskLevel.LOW)
                .decision(Decision.APPROVED)
                .summary("No fraud indicators detected.")
                .triggeredRules(Collections.emptyList())
                .build();

        when(transactionRiskService.evaluateTransactionRisk(any())).thenReturn(decision);

        String result = fraudDetectionService.checkFraud(1L, new BigDecimal("5000.00"));
        assertNull(result);
    }

    @Test
    @DisplayName("Rejected decision -> checkFraud returns fraud reason")
    void testRejectedTransaction_ShouldReturnFraudReason() {
        TriggeredRule rule = TriggeredRule.builder()
                .ruleId("RULE-001")
                .ruleName("HIGH_AMOUNT")
                .severity(RuleSeverity.HIGH)
                .description("Transaction amount exceeded configured threshold.")
                .build();

        FraudDecision decision = FraudDecision.builder()
                .riskScore(80)
                .riskLevel(RiskLevel.CRITICAL)
                .decision(Decision.REJECTED)
                .summary("1 fraud indicator detected.")
                .triggeredRules(List.of(rule))
                .build();

        when(transactionRiskService.evaluateTransactionRisk(any())).thenReturn(decision);

        String result = fraudDetectionService.checkFraud(1L, new BigDecimal("75000.00"));
        assertNotNull(result);
        assertTrue(result.contains("exceeded"));
    }
}
