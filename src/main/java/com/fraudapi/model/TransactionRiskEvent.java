package com.fraudapi.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Represents an individual triggered fraud rule event associated with a transaction.
 */
@Entity
@Table(name = "transaction_risk_events",
       indexes = {
           @Index(name = "idx_risk_event_txn_id", columnList = "transaction_id")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRiskEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Associated transaction. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

    /** Unique rule identifier (e.g. "RULE-001"). */
    @Column(name = "rule_id", nullable = false, length = 50)
    private String ruleId;

    /** Rule name (e.g. "HIGH_AMOUNT"). */
    @Column(name = "rule_name", nullable = false, length = 100)
    private String ruleName;

    /** Rule category (e.g. "TRANSACTION", "VELOCITY"). */
    @Column(name = "category", nullable = false, length = 50)
    private String category;

    /** Rule severity classification (e.g. "LOW", "MEDIUM", "HIGH", "CRITICAL"). */
    @Column(name = "severity", nullable = false, length = 20)
    private String severity;

    /** Risk points contributed by this rule. */
    @Column(name = "points", nullable = false)
    private Integer points;

    /** Description of why the rule fired. */
    @Column(name = "description", nullable = false, length = 255)
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
