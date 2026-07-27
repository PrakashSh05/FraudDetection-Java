package com.fraudapi.service;

import com.fraudapi.constants.Decision;
import com.fraudapi.constants.RiskLevel;
import com.fraudapi.dto.FraudDecision;
import com.fraudapi.dto.TriggeredRule;
import com.fraudapi.engine.DecisionEngine;
import com.fraudapi.engine.FraudRule;
import com.fraudapi.engine.TransactionContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Orchestration service executing registered {@link FraudRule} strategy implementations,
 * accumulating risk scores, determining risk levels, and delegating to {@link DecisionEngine}.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionRiskService {

    private static final int MAX_SCORE = 100;
    private static final int LOW_SCORE_MAX = 29;
    private static final int MEDIUM_SCORE_MAX = 59;
    private static final int HIGH_SCORE_MAX = 79;

    private final List<FraudRule> rules;
    private final DecisionEngine decisionEngine;

    /**
     * Evaluates all registered fraud rules against the provided transaction context.
     *
     * @param context the context payload for transaction evaluation
     * @return the complete {@link FraudDecision} outcome
     */
    public FraudDecision evaluateTransactionRisk(TransactionContext context) {
        long startTime = System.currentTimeMillis();
        Long userId = context != null ? context.getUserId() : null;

        log.info("Starting transaction risk evaluation for userId={}", userId);

        List<TriggeredRule> triggeredRules = new ArrayList<>();
        int accumulatedScore = 0;

        if (rules != null) {
            for (FraudRule rule : rules) {
                try {
                    Optional<TriggeredRule> result = rule.evaluate(context);
                    if (result.isPresent()) {
                        TriggeredRule triggered = result.get();
                        triggeredRules.add(triggered);
                        accumulatedScore += triggered.getPoints();
                        log.debug("Rule [{}] triggered with {} points for userId={}",
                                rule.getRuleId(), triggered.getPoints(), userId);
                    }
                } catch (Exception ex) {
                    log.error("Error executing fraud rule [{}] for userId={}: {}",
                            rule.getRuleId(), userId, ex.getMessage(), ex);
                }
            }
        }

        int finalScore = Math.min(accumulatedScore, MAX_SCORE);
        RiskLevel riskLevel = determineRiskLevel(finalScore);
        Decision decision = decisionEngine.determineDecision(riskLevel);
        String summary = generateSummary(triggeredRules, riskLevel);

        long processingTimeMs = System.currentTimeMillis() - startTime;

        log.info("Completed risk evaluation for userId={}: score={}, level={}, decision={}, duration={}ms",
                userId, finalScore, riskLevel, decision, processingTimeMs);

        return FraudDecision.builder()
                .riskScore(finalScore)
                .riskLevel(riskLevel)
                .decision(decision)
                .summary(summary)
                .processingTimeMs(processingTimeMs)
                .triggeredRules(triggeredRules)
                .build();
    }

    /**
     * Determines the qualitative risk level based on the numeric risk score.
     */
    private RiskLevel determineRiskLevel(int score) {
        if (score <= LOW_SCORE_MAX) {
            return RiskLevel.LOW;
        } else if (score <= MEDIUM_SCORE_MAX) {
            return RiskLevel.MEDIUM;
        } else if (score <= HIGH_SCORE_MAX) {
            return RiskLevel.HIGH;
        } else {
            return RiskLevel.CRITICAL;
        }
    }

    /**
     * Generates a concise human-readable summary of the evaluation result,
     * including the names of triggered rules for analyst visibility.
     */
    private String generateSummary(List<TriggeredRule> triggeredRules, RiskLevel riskLevel) {
        if (triggeredRules.isEmpty()) {
            return "No fraud indicators detected.";
        }

        String ruleNames = triggeredRules.stream()
                .map(TriggeredRule::getRuleName)
                .collect(java.util.stream.Collectors.joining(", "));

        int count = triggeredRules.size();
        String indicator = count == 1 ? "fraud indicator" : "fraud indicators";
        return String.format("%d %s detected [%s]. Risk level: %s.", count, indicator, ruleNames, riskLevel);
    }
}
