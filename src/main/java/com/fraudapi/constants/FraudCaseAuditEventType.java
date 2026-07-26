package com.fraudapi.constants;

/**
 * Enumeration representing the specific event type recorded in a fraud case audit trail.
 */
public enum FraudCaseAuditEventType {

    /** Case automatically opened by system. */
    CASE_CREATED,

    /** Case assigned to an analyst. */
    CASE_ASSIGNED,

    /** Case status transitioned. */
    STATUS_CHANGED,

    /** Analyst review notes appended or updated. */
    NOTES_UPDATED,

    /** Case resolution outcome recorded. */
    CASE_RESOLVED,

    /** Case audit trail finalized and closed. */
    CASE_CLOSED
}
