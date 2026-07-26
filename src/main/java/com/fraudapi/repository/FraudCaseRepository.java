package com.fraudapi.repository;

import com.fraudapi.constants.FraudCaseStatus;
import com.fraudapi.model.FraudCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link FraudCase} persistence, dynamic filtering, and queue statistics.
 */
@Repository
public interface FraudCaseRepository extends JpaRepository<FraudCase, Long>, JpaSpecificationExecutor<FraudCase> {

    /**
     * Finds the fraud case associated with a specific transaction ID.
     *
     * @param transactionId the transaction ID
     * @return optional fraud case
     */
    Optional<FraudCase> findByTransactionId(Long transactionId);

    /**
     * Finds all fraud cases with a specific status.
     *
     * @param status the case status
     * @return list of fraud cases
     */
    List<FraudCase> findByStatus(FraudCaseStatus status);

    /**
     * Checks if a fraud case exists for a transaction ID.
     *
     * @param transactionId the transaction ID
     * @return true if exists, false otherwise
     */
    boolean existsByTransactionId(Long transactionId);

    /**
     * Aggregates count of fraud cases grouped by status.
     *
     * @return list of Object array containing [statusEnum, countLong]
     */
    @Query("SELECT fc.status, COUNT(fc) FROM FraudCase fc GROUP BY fc.status")
    List<Object[]> countByStatusGrouped();

    /**
     * Aggregates count of fraud cases grouped by priority.
     *
     * @return list of Object array containing [priorityEnum, countLong]
     */
    @Query("SELECT fc.priority, COUNT(fc) FROM FraudCase fc GROUP BY fc.priority")
    List<Object[]> countByPriorityGrouped();
}
