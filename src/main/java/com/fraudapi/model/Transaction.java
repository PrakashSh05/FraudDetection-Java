package com.fraudapi.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a financial transaction, including its persisted risk evaluation result.
 */
@Entity
@Table(name = "transactions",
       indexes = {
           @Index(name = "idx_txn_user_id", columnList = "user_id"),
           @Index(name = "idx_txn_status",  columnList = "status"),
           @Index(name = "idx_txn_created_at", columnList = "created_at"),
           @Index(name = "idx_txn_decision", columnList = "decision"),
           @Index(name = "idx_txn_risk_level", columnList = "risk_level")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The owner of the transaction. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Transaction amount (always positive). */
    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    /** DEBIT or CREDIT. */
    @Column(name = "transaction_type", nullable = false, length = 20)
    private String transactionType;

    /** APPROVED or FLAGGED. */
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    /** Human-readable reason when status = FLAGGED, null otherwise. */
    @Column(name = "fraud_reason", length = 255)
    private String fraudReason;

    /** Accumulated numeric risk score (0 to 100). */
    @Column(name = "risk_score")
    private Integer riskScore;

    /** Qualitative risk classification level (LOW, MEDIUM, HIGH, CRITICAL). */
    @Column(name = "risk_level", length = 20)
    private String riskLevel;

    /** Recommended business decision (APPROVED, MONITOR, REVIEW, REJECTED). */
    @Column(name = "decision", length = 20)
    private String decision;

    /** Engine processing time in milliseconds. */
    @Column(name = "processing_time_ms")
    private Long processingTimeMs;

    /** Timestamp when the risk evaluation was performed. */
    @Column(name = "evaluation_timestamp")
    private LocalDateTime evaluationTimestamp;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
