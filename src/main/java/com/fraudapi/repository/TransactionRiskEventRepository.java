package com.fraudapi.repository;

import com.fraudapi.model.TransactionRiskEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link TransactionRiskEvent} persistence and retrieval.
 */
@Repository
public interface TransactionRiskEventRepository extends JpaRepository<TransactionRiskEvent, Long> {

    /**
     * Finds all triggered risk events for a specific transaction ID.
     *
     * @param transactionId the transaction ID
     * @return list of risk events
     */
    List<TransactionRiskEvent> findByTransactionId(Long transactionId);
}
