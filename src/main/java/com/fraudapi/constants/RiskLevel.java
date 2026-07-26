package com.fraudapi.constants;

/**
 * Enumeration representing the qualitative risk classification tier of a transaction.
 */
public enum RiskLevel {

    /** Minimal risk detected (Score range: 0–29). */
    LOW,

    /** Moderate risk detected (Score range: 30–59). */
    MEDIUM,

    /** High risk detected (Score range: 60–79). */
    HIGH,

    /** Severe or critical risk detected (Score range: 80–100). */
    CRITICAL
}
