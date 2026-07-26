package com.fraudapi.service;

import com.fraudapi.constants.FraudCaseAuditEventType;
import com.fraudapi.constants.FraudCasePriority;
import com.fraudapi.constants.FraudCaseStatus;
import com.fraudapi.dto.cases.*;
import com.fraudapi.dto.investigation.RiskEvaluationDetails;
import com.fraudapi.dto.investigation.TransactionDetails;
import com.fraudapi.dto.investigation.TriggeredRuleDetails;
import com.fraudapi.exception.TransactionNotFoundException;
import com.fraudapi.model.FraudCase;
import com.fraudapi.model.Transaction;
import com.fraudapi.model.TransactionRiskEvent;
import com.fraudapi.repository.FraudCaseRepository;
import com.fraudapi.repository.FraudCaseSpecification;
import com.fraudapi.repository.TransactionRiskEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Business service managing the lifecycle, assignment, status transitions, resolution,
 * dynamic queue filtering, and queue statistics for fraud cases.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FraudCaseService {

    private final FraudCaseRepository fraudCaseRepository;
    private final TransactionRiskEventRepository transactionRiskEventRepository;
    private final FraudCaseAuditService fraudCaseAuditService;

    /**
     * Retrieves a paginated and dynamically filtered list of fraud cases for the analyst queue.
     */
    @Transactional(readOnly = true)
    public Page<FraudCaseSummaryResponse> getCaseQueue(
            FraudCaseStatus status,
            FraudCasePriority priority,
            String assignedTo,
            Long transactionId,
            String riskLevel,
            Long caseId,
            Pageable pageable) {

        log.debug("Fetching fraud case queue with filters: status={}, priority={}, assignedTo={}, transactionId={}, riskLevel={}, caseId={}, pageable={}",
                status, priority, assignedTo, transactionId, riskLevel, caseId, pageable);

        Specification<FraudCase> spec = FraudCaseSpecification.withFilters(
                status, priority, assignedTo, transactionId, riskLevel, caseId);

        return fraudCaseRepository.findAll(spec, pageable)
                .map(this::toSummaryResponse);
    }

    /**
     * Aggregates statistics for the fraud case queue breakdown by status and priority.
     */
    @Transactional(readOnly = true)
    public FraudCaseQueueSummaryResponse getQueueSummary() {
        log.debug("Fetching fraud case queue statistics summary");
        long total = fraudCaseRepository.count();

        Map<FraudCaseStatus, Long> statusMap = new HashMap<>();
        List<Object[]> statusRows = fraudCaseRepository.countByStatusGrouped();
        for (Object[] row : statusRows) {
            if (row[0] != null) {
                statusMap.put((FraudCaseStatus) row[0], ((Number) row[1]).longValue());
            }
        }

        Map<FraudCasePriority, Long> priorityMap = new HashMap<>();
        List<Object[]> priorityRows = fraudCaseRepository.countByPriorityGrouped();
        for (Object[] row : priorityRows) {
            if (row[0] != null) {
                priorityMap.put((FraudCasePriority) row[0], ((Number) row[1]).longValue());
            }
        }

        return new FraudCaseQueueSummaryResponse(
                total,
                statusMap.getOrDefault(FraudCaseStatus.OPEN, 0L),
                statusMap.getOrDefault(FraudCaseStatus.ASSIGNED, 0L),
                statusMap.getOrDefault(FraudCaseStatus.UNDER_REVIEW, 0L),
                statusMap.getOrDefault(FraudCaseStatus.APPROVED, 0L),
                statusMap.getOrDefault(FraudCaseStatus.DECLINED, 0L),
                statusMap.getOrDefault(FraudCaseStatus.ESCALATED, 0L),
                statusMap.getOrDefault(FraudCaseStatus.CLOSED, 0L),
                priorityMap.getOrDefault(FraudCasePriority.CRITICAL, 0L),
                priorityMap.getOrDefault(FraudCasePriority.HIGH, 0L),
                priorityMap.getOrDefault(FraudCasePriority.MEDIUM, 0L),
                priorityMap.getOrDefault(FraudCasePriority.LOW, 0L)
        );
    }

    /**
     * Retrieves detailed information and risk telemetry for a single fraud case.
     */
    @Transactional(readOnly = true)
    public FraudCaseDetailResponse getCaseDetails(Long caseId) {
        log.debug("Fetching fraud case details for caseId={}", caseId);
        FraudCase fraudCase = findCaseOrThrow(caseId);
        return toDetailResponse(fraudCase);
    }

    /**
     * Assigns an open or active fraud case to a compliance analyst and records audit logs.
     */
    @Transactional
    public FraudCaseDetailResponse assignCase(Long caseId, String assignedTo) {
        log.info("Assigning caseId={} to analyst '{}'", caseId, assignedTo);
        FraudCase fraudCase = findCaseOrThrow(caseId);

        if (FraudCaseStatus.CLOSED.equals(fraudCase.getStatus())) {
            throw new IllegalArgumentException("Cannot assign a CLOSED case");
        }

        String oldAssignee = fraudCase.getAssignedTo();
        String oldStatus = fraudCase.getStatus().name();

        fraudCase.setAssignedTo(assignedTo);
        if (FraudCaseStatus.OPEN.equals(fraudCase.getStatus())) {
            fraudCase.setStatus(FraudCaseStatus.ASSIGNED);
        }

        FraudCase updated = fraudCaseRepository.save(fraudCase);

        fraudCaseAuditService.recordAudit(updated, FraudCaseAuditEventType.CASE_ASSIGNED, oldAssignee, assignedTo, assignedTo);
        if (!oldStatus.equals(updated.getStatus().name())) {
            fraudCaseAuditService.recordAudit(updated, FraudCaseAuditEventType.STATUS_CHANGED, oldStatus, updated.getStatus().name(), assignedTo);
        }

        return toDetailResponse(updated);
    }

    /**
     * Updates the status of a fraud case adhering to workflow state machine constraints and records audit logs.
     */
    @Transactional
    public FraudCaseDetailResponse updateCaseStatus(Long caseId, FraudCaseStatus newStatus) {
        log.info("Updating status for caseId={} to newStatus={}", caseId, newStatus);
        FraudCase fraudCase = findCaseOrThrow(caseId);

        String oldStatus = fraudCase.getStatus().name();
        validateStatusTransition(fraudCase.getStatus(), newStatus);

        fraudCase.setStatus(newStatus);
        if (FraudCaseStatus.CLOSED.equals(newStatus) && fraudCase.getClosedAt() == null) {
            fraudCase.setClosedAt(LocalDateTime.now());
        }

        FraudCase updated = fraudCaseRepository.save(fraudCase);
        fraudCaseAuditService.recordAudit(updated, FraudCaseAuditEventType.STATUS_CHANGED, oldStatus, newStatus.name(), fraudCase.getAssignedTo());

        return toDetailResponse(updated);
    }

    /**
     * Appends or updates analyst review notes on a fraud case and records audit logs.
     */
    @Transactional
    public FraudCaseDetailResponse updateCaseNotes(Long caseId, String reviewNotes) {
        log.info("Updating notes for caseId={}", caseId);
        FraudCase fraudCase = findCaseOrThrow(caseId);

        if (FraudCaseStatus.CLOSED.equals(fraudCase.getStatus())) {
            throw new IllegalArgumentException("Cannot update notes on a CLOSED case");
        }

        String oldNotes = fraudCase.getReviewNotes();
        fraudCase.setReviewNotes(reviewNotes);
        FraudCase updated = fraudCaseRepository.save(fraudCase);

        fraudCaseAuditService.recordAudit(updated, FraudCaseAuditEventType.NOTES_UPDATED, oldNotes, reviewNotes, fraudCase.getAssignedTo());
        return toDetailResponse(updated);
    }

    /**
     * Resolves and finalizes a fraud case, stashing resolution audit entries.
     */
    @Transactional
    public FraudCaseDetailResponse resolveCase(Long caseId, String resolution, FraudCaseStatus status) {
        log.info("Resolving caseId={} with status={} and resolution='{}'", caseId, status, resolution);
        FraudCase fraudCase = findCaseOrThrow(caseId);

        if (FraudCaseStatus.CLOSED.equals(fraudCase.getStatus())) {
            throw new IllegalArgumentException("Case is already CLOSED");
        }

        if (!List.of(FraudCaseStatus.APPROVED, FraudCaseStatus.DECLINED, FraudCaseStatus.ESCALATED, FraudCaseStatus.CLOSED).contains(status)) {
            throw new IllegalArgumentException("Resolution status must be APPROVED, DECLINED, ESCALATED, or CLOSED");
        }

        String oldStatus = fraudCase.getStatus().name();
        fraudCase.setResolution(resolution);
        fraudCase.setStatus(status);
        fraudCase.setClosedAt(LocalDateTime.now());

        FraudCase updated = fraudCaseRepository.save(fraudCase);

        fraudCaseAuditService.recordAudit(updated, FraudCaseAuditEventType.CASE_RESOLVED, oldStatus, status.name(), fraudCase.getAssignedTo());
        fraudCaseAuditService.recordAudit(updated, FraudCaseAuditEventType.CASE_CLOSED, null, status.name(), fraudCase.getAssignedTo());

        return toDetailResponse(updated);
    }

    // ------------------------------------------------------------------ //
    //  Helper & Validation Methods                                         //
    // ------------------------------------------------------------------ //

    private FraudCase findCaseOrThrow(Long caseId) {
        return fraudCaseRepository.findById(caseId)
                .orElseThrow(() -> new TransactionNotFoundException("Fraud case not found with ID: " + caseId));
    }

    private void validateStatusTransition(FraudCaseStatus current, FraudCaseStatus target) {
        if (current == target) {
            return;
        }

        if (FraudCaseStatus.CLOSED.equals(current)) {
            throw new IllegalArgumentException("Cannot transition out of CLOSED status");
        }

        boolean valid = switch (current) {
            case OPEN -> List.of(FraudCaseStatus.ASSIGNED, FraudCaseStatus.UNDER_REVIEW, FraudCaseStatus.APPROVED, FraudCaseStatus.DECLINED, FraudCaseStatus.ESCALATED, FraudCaseStatus.CLOSED).contains(target);
            case ASSIGNED -> List.of(FraudCaseStatus.UNDER_REVIEW, FraudCaseStatus.APPROVED, FraudCaseStatus.DECLINED, FraudCaseStatus.ESCALATED, FraudCaseStatus.CLOSED).contains(target);
            case UNDER_REVIEW -> List.of(FraudCaseStatus.APPROVED, FraudCaseStatus.DECLINED, FraudCaseStatus.ESCALATED, FraudCaseStatus.CLOSED).contains(target);
            case APPROVED, DECLINED, ESCALATED -> FraudCaseStatus.CLOSED.equals(target);
            case CLOSED -> false;
        };

        if (!valid) {
            throw new IllegalArgumentException("Invalid status transition from " + current + " to " + target);
        }
    }

    private FraudCaseSummaryResponse toSummaryResponse(FraudCase fc) {
        Transaction txn = fc.getTransaction();
        return new FraudCaseSummaryResponse(
                fc.getId(),
                txn != null ? txn.getId() : null,
                txn != null && txn.getUser() != null ? txn.getUser().getId() : null,
                txn != null ? txn.getAmount() : null,
                txn != null ? txn.getTransactionType() : null,
                txn != null ? txn.getRiskScore() : null,
                txn != null ? txn.getRiskLevel() : null,
                fc.getStatus(),
                fc.getPriority(),
                fc.getAssignedTo(),
                fc.getOpenedAt(),
                fc.getCreatedAt()
        );
    }

    private FraudCaseDetailResponse toDetailResponse(FraudCase fc) {
        Transaction txn = fc.getTransaction();

        TransactionDetails txnDetails = txn != null ? new TransactionDetails(
                txn.getId(),
                txn.getUser() != null ? txn.getUser().getId() : null,
                txn.getAmount(),
                txn.getTransactionType(),
                txn.getStatus(),
                txn.getCreatedAt()
        ) : null;

        RiskEvaluationDetails evalDetails = txn != null ? new RiskEvaluationDetails(
                txn.getRiskScore(),
                txn.getRiskLevel(),
                txn.getDecision(),
                txn.getProcessingTimeMs(),
                txn.getEvaluationTimestamp()
        ) : null;

        List<TriggeredRuleDetails> triggeredRules = txn != null ?
                transactionRiskEventRepository.findByTransactionId(txn.getId()).stream()
                        .map(e -> new TriggeredRuleDetails(
                                e.getRuleId(),
                                e.getRuleName(),
                                e.getCategory(),
                                e.getSeverity(),
                                e.getPoints(),
                                e.getDescription()
                        ))
                        .collect(Collectors.toList()) : List.of();

        return new FraudCaseDetailResponse(
                fc.getId(),
                fc.getStatus(),
                fc.getPriority(),
                fc.getAssignedTo(),
                fc.getOpenedAt(),
                fc.getClosedAt(),
                fc.getResolution(),
                fc.getReviewNotes(),
                fc.getCreatedAt(),
                fc.getUpdatedAt(),
                txnDetails,
                evalDetails,
                triggeredRules
        );
    }
}
