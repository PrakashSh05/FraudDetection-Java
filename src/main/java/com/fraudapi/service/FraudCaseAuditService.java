package com.fraudapi.service;

import com.fraudapi.constants.FraudCaseAuditEventType;
import com.fraudapi.dto.cases.FraudCaseAuditResponse;
import com.fraudapi.model.FraudCase;
import com.fraudapi.model.FraudCaseAudit;
import com.fraudapi.repository.FraudCaseAuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service managing immutable audit records and timeline retrieval for fraud cases.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FraudCaseAuditService {

    private final FraudCaseAuditRepository auditRepository;

    /**
     * Records an immutable audit log entry for a fraud case action.
     * Centralized helper used by service operations.
     */
    @Transactional
    public void recordAudit(FraudCase fraudCase, FraudCaseAuditEventType eventType, String oldValue, String newValue, String performedBy) {
        String actor = (performedBy != null && !performedBy.isBlank()) ? performedBy : "SYSTEM";

        FraudCaseAudit audit = FraudCaseAudit.builder()
                .fraudCase(fraudCase)
                .eventType(eventType)
                .oldValue(oldValue)
                .newValue(newValue)
                .performedBy(actor)
                .build();

        auditRepository.save(audit);
        log.info("Recorded audit event [{}] for caseId={} by {}", eventType, fraudCase.getId(), actor);
    }

    /**
     * Retrieves the complete ordered audit timeline for a fraud case.
     */
    @Transactional(readOnly = true)
    public List<FraudCaseAuditResponse> getCaseTimeline(Long caseId) {
        log.debug("Fetching audit timeline for caseId={}", caseId);
        return auditRepository.findByFraudCaseIdOrderByTimestampAsc(caseId).stream()
                .map(audit -> new FraudCaseAuditResponse(
                        audit.getId(),
                        audit.getEventType(),
                        audit.getOldValue(),
                        audit.getNewValue(),
                        audit.getPerformedBy(),
                        audit.getTimestamp()
                ))
                .collect(Collectors.toList());
    }
}
