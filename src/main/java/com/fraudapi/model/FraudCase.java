package com.fraudapi.model;

import com.fraudapi.constants.FraudCasePriority;
import com.fraudapi.constants.FraudCaseStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Represents a manual review fraud case created for high-risk transactions.
 */
@Entity
@Table(name = "fraud_cases",
       indexes = {
           @Index(name = "idx_fraud_case_status", columnList = "status"),
           @Index(name = "idx_fraud_case_priority", columnList = "priority")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FraudCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Associated transaction (1-to-1 relationship). */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transaction_id", unique = true, nullable = false)
    private Transaction transaction;

    /** Current lifecycle status of the case. */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private FraudCaseStatus status;

    /** Priority level derived from transaction risk score. */
    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 20)
    private FraudCasePriority priority;

    /** Analyst assigned to review the case (nullable until assigned). */
    @Column(name = "assigned_to", length = 100)
    private String assignedTo;

    /** Timestamp when the case was opened. */
    @Column(name = "opened_at", nullable = false)
    private LocalDateTime openedAt;

    /** Timestamp when the case review was finalized (nullable). */
    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    /** Resolution outcome summary (nullable). */
    @Column(name = "resolution", length = 255)
    private String resolution;

    /** Detailed notes recorded by compliance analyst (nullable). */
    @Column(name = "review_notes", length = 1000)
    private String reviewNotes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
