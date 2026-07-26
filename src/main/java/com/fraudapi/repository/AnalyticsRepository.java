package com.fraudapi.repository;

import com.fraudapi.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface providing optimized aggregation queries for risk analytics.
 */
@Repository
public interface AnalyticsRepository extends JpaRepository<Transaction, Long> {

    /**
     * Aggregates count of transactions grouped by business decision.
     *
     * @return list of Object array containing [decisionString, countLong]
     */
    @Query("SELECT t.decision, COUNT(t) FROM Transaction t WHERE t.decision IS NOT NULL GROUP BY t.decision")
    List<Object[]> countByDecisionGrouped();

    /**
     * Aggregates count of transactions grouped by risk level.
     *
     * @return list of Object array containing [riskLevelString, countLong]
     */
    @Query("SELECT t.riskLevel, COUNT(t) FROM Transaction t WHERE t.riskLevel IS NOT NULL GROUP BY t.riskLevel")
    List<Object[]> countByRiskLevelGrouped();

    /**
     * Calculates the overall average risk score across all transactions.
     *
     * @return the average risk score as Double
     */
    @Query("SELECT COALESCE(AVG(t.riskScore), 0.0) FROM Transaction t")
    Double getAverageRiskScore();

    /**
     * Aggregates top triggered rules by frequency.
     *
     * @return list of Object array containing [ruleId, ruleName, countLong]
     */
    @Query("SELECT e.ruleId, e.ruleName, COUNT(e) FROM TransactionRiskEvent e " +
           "GROUP BY e.ruleId, e.ruleName ORDER BY COUNT(e) DESC")
    List<Object[]> findTopTriggeredRules();

    /**
     * Aggregates daily transaction volume and average risk score.
     *
     * @return list of Object array containing [date, countLong, avgScoreDouble]
     */
    @Query(value = "SELECT CAST(t.created_at AS DATE) AS event_date, COUNT(t.id) AS total_txns, AVG(COALESCE(t.risk_score, 0)) AS avg_score " +
                   "FROM transactions t GROUP BY CAST(t.created_at AS DATE) ORDER BY event_date ASC",
           nativeQuery = true)
    List<Object[]> getDailyTrendMetrics();
}
