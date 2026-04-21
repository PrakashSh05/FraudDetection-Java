package com.fraudapi.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a financial transaction, including its fraud evaluation result.
 */
@Entity
@Table(name = "transactions",
       indexes = {
           @Index(name = "idx_txn_user_id", columnList = "user_id"),
           @Index(name = "idx_txn_status",  columnList = "status"),
           @Index(name = "idx_txn_created_at", columnList = "created_at")
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

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
