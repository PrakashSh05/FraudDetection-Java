package com.fraudapi.constants;

/**
 * Enumeration representing the business decision outcome for a transaction evaluation.
 */
public enum Decision {

    /** Transaction risk is low and operation is allowed to proceed normally. */
    APPROVED,

    /** Transaction is allowed to proceed but flagged for heightened automated monitoring. */
    MONITOR,

    /** Transaction is held or flagged for manual compliance/analyst review. */
    REVIEW,

    /** Transaction risk is critical and the request is blocked. */
    REJECTED
}
