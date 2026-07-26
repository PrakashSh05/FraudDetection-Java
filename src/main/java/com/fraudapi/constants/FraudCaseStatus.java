package com.fraudapi.constants;

/**
 * Enumeration representing the operational lifecycle status of a fraud case.
 */
public enum FraudCaseStatus {

    /** Case has been automatically created and is awaiting assignment. */
    OPEN,

    /** Case has been assigned to a compliance analyst. */
    ASSIGNED,

    /** Case is actively under manual review. */
    UNDER_REVIEW,

    /** Transaction cleared after manual review. */
    APPROVED,

    /** Transaction confirmed fraudulent and declined after manual review. */
    DECLINED,

    /** Case escalated to senior risk management. */
    ESCALATED,

    /** Case review has been finalized and closed. */
    CLOSED
}
