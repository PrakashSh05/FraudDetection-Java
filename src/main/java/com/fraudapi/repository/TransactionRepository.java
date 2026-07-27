package com.fraudapi.repository;

import com.fraudapi.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data-access layer for {@link Transaction} entities.
 *
 * <p>Custom queries support:
 * <ul>
 *   <li>Velocity check (recent transactions per user)</li>
 *   <li>All flagged transactions (admin view)</li>
 *   <li>Full history per user</li>
 * </ul>
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Returns all transactions for a given user created after {@code after}.
     * Used by the velocity fraud rule to count recent transactions.
     */
    List<Transaction> findByUserIdAndCreatedAtAfter(Long userId, LocalDateTime after);

    /**
     * Returns all transactions for a user, newest first.
     */
    List<Transaction> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Returns all transactions with status = FLAGGED, newest first.
     */
    List<Transaction> findByStatusOrderByCreatedAtDesc(String status);

    /**
     * Count of transactions for a user within a given time window.
     * Used as a more efficient alternative to loading the full list for velocity checks.
     */
    @Query("SELECT COUNT(t) FROM Transaction t " +
           "WHERE t.user.id = :userId AND t.createdAt > :after")
    long countRecentTransactions(@Param("userId") Long userId,
                                  @Param("after") LocalDateTime after);

    /**
     * Count of transactions for a user with the exact same amount within a given time window.
     * Used by the RepeatedAmountRule to detect structuring / split-payment fraud.
     */
    @Query("SELECT COUNT(t) FROM Transaction t " +
           "WHERE t.user.id = :userId AND t.amount = :amount AND t.createdAt > :after")
    long countRepeatedAmountTransactions(@Param("userId") Long userId,
                                          @Param("amount") java.math.BigDecimal amount,
                                          @Param("after") LocalDateTime after);
}
