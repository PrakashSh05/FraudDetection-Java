package com.fraudapi.constants;

/**
 * Enumeration representing the review priority of a fraud case.
 */
public enum FraudCasePriority {

    /** Low priority review. */
    LOW,

    /** Medium priority review. */
    MEDIUM,

    /** High priority review. */
    HIGH,

    /** Critical priority review. */
    CRITICAL;

    /**
     * Maps a {@link RiskLevel} to its corresponding {@link FraudCasePriority}.
     *
     * @param riskLevel the risk level tier
     * @return the corresponding fraud case priority
     */
    public static FraudCasePriority fromRiskLevel(RiskLevel riskLevel) {
        if (riskLevel == null) {
            return MEDIUM;
        }

        return switch (riskLevel) {
            case LOW -> LOW;
            case MEDIUM -> MEDIUM;
            case HIGH -> HIGH;
            case CRITICAL -> CRITICAL;
        };
    }
}
