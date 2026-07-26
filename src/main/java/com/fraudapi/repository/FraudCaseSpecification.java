package com.fraudapi.repository;

import com.fraudapi.constants.FraudCasePriority;
import com.fraudapi.constants.FraudCaseStatus;
import com.fraudapi.model.FraudCase;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class building dynamic JPA specifications for filtering the fraud case queue.
 */
public class FraudCaseSpecification {

    private FraudCaseSpecification() {
        // Utility class
    }

    /**
     * Constructs a dynamic Specification based on optional filter parameters.
     */
    public static Specification<FraudCase> withFilters(
            FraudCaseStatus status,
            FraudCasePriority priority,
            String assignedTo,
            Long transactionId,
            String riskLevel,
            Long caseId) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (caseId != null) {
                predicates.add(cb.equal(root.get("id"), caseId));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (priority != null) {
                predicates.add(cb.equal(root.get("priority"), priority));
            }

            if (assignedTo != null && !assignedTo.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("assignedTo")), assignedTo.trim().toLowerCase()));
            }

            if (transactionId != null) {
                predicates.add(cb.equal(root.get("transaction").get("id"), transactionId));
            }

            if (riskLevel != null && !riskLevel.isBlank()) {
                predicates.add(cb.equal(cb.upper(root.get("transaction").get("riskLevel")), riskLevel.trim().toUpperCase()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
