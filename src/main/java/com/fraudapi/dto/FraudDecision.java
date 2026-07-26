package com.fraudapi.dto;

import com.fraudapi.constants.Decision;
import com.fraudapi.constants.RiskLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Payload encapsulating the complete fraud decision outcome, risk scoring, and rule telemetry.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FraudDecision {

    /** Accumulated numeric risk score (0 to 100). */
    private int riskScore;

    /** Qualitative risk classification level. */
    private RiskLevel riskLevel;

    /** Final business decision recommendation. */
    private Decision decision;

    /** Backend-generated readable summary of the evaluation result. */
    private String summary;

    /** Processing duration of the risk evaluation engine in milliseconds. */
    private long processingTimeMs;

    /** List of individual rules that were triggered during evaluation. */
    private List<TriggeredRule> triggeredRules;
}
