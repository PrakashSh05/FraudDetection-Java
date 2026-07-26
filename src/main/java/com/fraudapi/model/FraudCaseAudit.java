package com.fraudapi.model;

import com.fraudapi.constants.FraudCaseAuditEventType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Immutable entity representing a single write-once audit log record for a fraud case action.
 */
@Entity
@Table(name = "fraud_case_audits",
       indexes = {
           @Index(name = "idx_audit_case_id", columnList = "fraud_case_id")
       })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class FraudCaseAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Associated fraud case. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fraud_case_id", nullable = false)
    private FraudCase fraudCase;

    /** Categorized audit event type. */
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 50)
    private FraudCaseAuditEventType eventType;

    /** Previous value prior to the action (nullable). */
    @Column(name = "old_value", length = 1000)
    private String oldValue;

    /** New value after the action (nullable). */
    @Column(name = "new_value", length = 1000)
    private String newValue;

    /** Actor or system component performing the action (default "SYSTEM"). */
    @Column(name = "performed_by", nullable = false, length = 100)
    private String performedBy;

    /** Immutable timestamp when the audit record was created. */
    @CreationTimestamp
    @Column(name = "timestamp", updatable = false)
    private LocalDateTime timestamp;
}
