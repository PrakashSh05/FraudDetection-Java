package com.fraudapi.service;

import com.fraudapi.dto.investigation.InvestigationResponse;
import com.fraudapi.dto.investigation.RiskEvaluationDetails;
import com.fraudapi.dto.investigation.TransactionDetails;
import com.fraudapi.dto.investigation.TriggeredRuleDetails;
import com.fraudapi.exception.TransactionNotFoundException;
import com.fraudapi.model.Transaction;
import com.fraudapi.model.TransactionRiskEvent;
import com.fraudapi.repository.TransactionRepository;
import com.fraudapi.repository.TransactionRiskEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Read-only service reconstructing complete fraud evaluation audit trails for investigation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class InvestigationService {

    private final TransactionRepository transactionRepository;
    private final TransactionRiskEventRepository transactionRiskEventRepository;

    /**
     * Retrieves the complete investigation audit trail for a specific transaction ID.
     *
     * @param transactionId the ID of the transaction to investigate
     * @return complete investigation report
     * @throws TransactionNotFoundException if transaction ID does not exist
     */
    public InvestigationResponse getInvestigationReport(Long transactionId) {
        log.debug("Generating investigation report for transactionId={}", transactionId);

        Transaction txn = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException(transactionId));

        TransactionDetails transactionDetails = new TransactionDetails(
                txn.getId(),
                txn.getUser() != null ? txn.getUser().getId() : null,
                txn.getAmount(),
                txn.getTransactionType(),
                txn.getStatus(),
                txn.getCreatedAt()
        );

        RiskEvaluationDetails evaluationDetails = new RiskEvaluationDetails(
                txn.getRiskScore(),
                txn.getRiskLevel(),
                txn.getDecision(),
                txn.getProcessingTimeMs(),
                txn.getEvaluationTimestamp()
        );

        List<TransactionRiskEvent> riskEvents = transactionRiskEventRepository.findByTransactionId(transactionId);

        List<TriggeredRuleDetails> triggeredRules = riskEvents.stream()
                .map(event -> new TriggeredRuleDetails(
                        event.getRuleId(),
                        event.getRuleName(),
                        event.getCategory(),
                        event.getSeverity(),
                        event.getPoints(),
                        event.getDescription()
                ))
                .collect(Collectors.toList());

        log.info("Generated investigation report for transactionId={}: score={}, level={}, decision={}, triggeredCount={}",
                transactionId, txn.getRiskScore(), txn.getRiskLevel(), txn.getDecision(), triggeredRules.size());

        return new InvestigationResponse(
                transactionDetails,
                evaluationDetails,
                triggeredRules
        );
    }
}
