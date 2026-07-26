package com.fraudapi.engine;

import com.fraudapi.constants.Decision;
import com.fraudapi.constants.RiskLevel;
import org.springframework.stereotype.Component;

/**
 * Component responsible solely for mapping a {@link RiskLevel} to its corresponding business {@link Decision}.
 */
@Component
public class DecisionEngine {

    /**
     * Determines the business decision based on the evaluated risk level.
     *
     * @param riskLevel the calculated risk level
     * @return the corresponding business decision
     */
    public Decision determineDecision(RiskLevel riskLevel) {
        if (riskLevel == null) {
            return Decision.APPROVED;
        }

        return switch (riskLevel) {
            case LOW -> Decision.APPROVED;
            case MEDIUM -> Decision.MONITOR;
            case HIGH -> Decision.REVIEW;
            case CRITICAL -> Decision.REJECTED;
        };
    }
}
