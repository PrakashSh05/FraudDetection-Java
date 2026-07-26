package com.fraudapi.repository;

import com.fraudapi.model.FraudCaseAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for immutable {@link FraudCaseAudit} records.
 */
@Repository
public interface FraudCaseAuditRepository extends JpaRepository<FraudCaseAudit, Long> {

    /**
     * Retrieves all audit records for a specific fraud case ordered by timestamp ascending.
     *
     * @param fraudCaseId the fraud case ID
     * @return ordered list of audit records
     */
    List<FraudCaseAudit> findByFraudCaseIdOrderByTimestampAsc(Long fraudCaseId);
}
