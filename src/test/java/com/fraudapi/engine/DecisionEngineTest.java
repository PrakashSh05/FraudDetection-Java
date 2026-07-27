package com.fraudapi.engine;

import com.fraudapi.constants.Decision;
import com.fraudapi.constants.RiskLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for {@link DecisionEngine}.
 */
@DisplayName("DecisionEngine Unit Tests")
class DecisionEngineTest {

    private DecisionEngine decisionEngine;

    @BeforeEach
    void setUp() {
        decisionEngine = new DecisionEngine();
    }

    @Test
    @DisplayName("RiskLevel.LOW -> Decision.APPROVED")
    void testEvaluate_LowRisk_ReturnsApproved() {
        Decision decision = decisionEngine.determineDecision(RiskLevel.LOW);
        assertEquals(Decision.APPROVED, decision);
    }

    @Test
    @DisplayName("RiskLevel.MEDIUM -> Decision.MONITOR")
    void testEvaluate_MediumRisk_ReturnsMonitor() {
        Decision decision = decisionEngine.determineDecision(RiskLevel.MEDIUM);
        assertEquals(Decision.MONITOR, decision);
    }

    @Test
    @DisplayName("RiskLevel.HIGH -> Decision.REVIEW")
    void testEvaluate_HighRisk_ReturnsReview() {
        Decision decision = decisionEngine.determineDecision(RiskLevel.HIGH);
        assertEquals(Decision.REVIEW, decision);
    }

    @Test
    @DisplayName("RiskLevel.CRITICAL -> Decision.REJECTED")
    void testEvaluate_CriticalRisk_ReturnsRejected() {
        Decision decision = decisionEngine.determineDecision(RiskLevel.CRITICAL);
        assertEquals(Decision.REJECTED, decision);
    }

    @Test
    @DisplayName("Null RiskLevel -> Decision.APPROVED (safe default)")
    void testEvaluate_NullRiskLevel_ReturnsApproved() {
        Decision decision = decisionEngine.determineDecision(null);
        assertEquals(Decision.APPROVED, decision);
    }
}
