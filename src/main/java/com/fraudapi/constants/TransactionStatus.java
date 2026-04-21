package com.fraudapi.constants;

/**
 * Allowed transaction status values.
 * Using constants avoids SQL injection from raw strings and enables easy refactoring.
 */
public final class TransactionStatus {

    public static final String APPROVED = "APPROVED";
    public static final String FLAGGED  = "FLAGGED";

    private TransactionStatus() {
        // Utility class — no instantiation
    }
}
