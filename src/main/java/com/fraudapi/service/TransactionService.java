package com.fraudapi.service;

import com.fraudapi.constants.*;
import com.fraudapi.dto.FraudDecision;
import com.fraudapi.dto.TransactionRequest;
import com.fraudapi.dto.TransactionResponse;
import com.fraudapi.engine.TransactionContext;
import com.fraudapi.exception.InsufficientBalanceException;
import com.fraudapi.exception.UserNotFoundException;
import com.fraudapi.model.FraudCase;
import com.fraudapi.model.Transaction;
import com.fraudapi.model.TransactionRiskEvent;
import com.fraudapi.model.User;
import com.fraudapi.repository.FraudCaseRepository;
import com.fraudapi.repository.TransactionRepository;
import com.fraudapi.repository.TransactionRiskEventRepository;
import com.fraudapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Core business service orchestrating financial transaction processing,
 * rule engine risk evaluation, persistence of risk events, balance adjustments,
 * and automated case creation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final TransactionRiskEventRepository transactionRiskEventRepository;
    private final TransactionRiskService transactionRiskService;
    private final FraudCaseRepository fraudCaseRepository;
    private final FraudCaseAuditService fraudCaseAuditService;

    /**
     * Creates and processes a financial transaction.
     */
    @Transactional
    public TransactionResponse createTransaction(TransactionRequest request) {
        log.info("Processing transaction request for userId={} type={} amount={}",
                request.getUserId(), request.getTransactionType(), request.getAmount());

        // Step 1 — validate user existence
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException(request.getUserId()));

        BigDecimal amount = request.getAmount();

        // Step 2 — construct evaluation context
        TransactionContext context = TransactionContext.builder()
                .userId(user.getId())
                .amount(amount)
                .transactionType(request.getTransactionType())
                .build();

        // Step 3 — evaluate risk using rule engine
        FraudDecision fraudDecision = transactionRiskService.evaluateTransactionRisk(context);
        Decision decision = fraudDecision.getDecision();

        // Step 4 — construct base transaction record
        Transaction txn = Transaction.builder()
                .user(user)
                .amount(amount)
                .transactionType(request.getTransactionType())
                .riskScore(fraudDecision.getRiskScore())
                .riskLevel(fraudDecision.getRiskLevel() != null ? fraudDecision.getRiskLevel().name() : "LOW")
                .decision(decision != null ? decision.name() : "APPROVED")
                .processingTimeMs(fraudDecision.getProcessingTimeMs())
                .evaluationTimestamp(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();

        BigDecimal balanceAfter = user.getBalance();
        String fraudReason = null;

        // Step 5 — process decision branches
        if (Decision.REJECTED.equals(decision)) {
            txn.setStatus(TransactionStatus.FLAGGED);
            fraudReason = "Transaction blocked by rule engine due to high-risk evaluation. Reason: "
                    + fraudDecision.getSummary();
            txn.setFraudReason(fraudReason);
            log.warn("Transaction REJECTED for userId={}: {}", user.getId(), fraudReason);
        } else if (Decision.REVIEW.equals(decision)) {
            txn.setStatus(TransactionStatus.FLAGGED);
            fraudReason = "Transaction flagged for manual compliance review. Reason: "
                    + fraudDecision.getSummary();
            txn.setFraudReason(fraudReason);
            log.info("Transaction REVIEW required for userId={}: {}", user.getId(), fraudReason);
        } else {
            if (Decision.MONITOR.equals(decision)) {
                log.info("Transaction MONITOR tier active for userId={}", user.getId());
            }

            if (TransactionType.DEBIT.equals(request.getTransactionType())) {
                if (user.getBalance().compareTo(amount) < 0) {
                    throw new InsufficientBalanceException(
                            user.getBalance().toPlainString(),
                            amount.toPlainString());
                }
                balanceAfter = user.getBalance().subtract(amount);
            } else {
                balanceAfter = user.getBalance().add(amount);
            }

            user.setBalance(balanceAfter);
            userRepository.save(user);

            txn.setStatus(TransactionStatus.APPROVED);
            log.info("Transaction APPROVED for userId={} newBalance={}", user.getId(), balanceAfter);
        }

        Transaction saved = transactionRepository.save(txn);

        // Step 6 — persist triggered rules as normalized risk events
        if (fraudDecision.getTriggeredRules() != null && !fraudDecision.getTriggeredRules().isEmpty()) {
            List<TransactionRiskEvent> events = fraudDecision.getTriggeredRules().stream()
                    .map(rule -> TransactionRiskEvent.builder()
                            .transaction(saved)
                            .ruleId(rule.getRuleId())
                            .ruleName(rule.getRuleName())
                            .category(rule.getCategory())
                            .severity(rule.getSeverity() != null ? rule.getSeverity().name() : null)
                            .points(rule.getPoints())
                            .description(rule.getDescription())
                            .build())
                    .collect(Collectors.toList());

            transactionRiskEventRepository.saveAll(events);
            log.info("Persisted {} risk event(s) for transaction ID={}", events.size(), saved.getId());
        }

        // Step 7 — create a FraudCase (APPROVED status for clean transactions, OPEN for flagged)
        FraudCasePriority priority = FraudCasePriority.fromRiskLevel(fraudDecision.getRiskLevel());
        FraudCaseStatus caseStatus = Decision.APPROVED.equals(decision) ? FraudCaseStatus.APPROVED : FraudCaseStatus.OPEN;

        FraudCase fraudCase = FraudCase.builder()
                .transaction(saved)
                .status(caseStatus)
                .priority(priority)
                .openedAt(LocalDateTime.now())
                .closedAt(Decision.APPROVED.equals(decision) ? LocalDateTime.now() : null)
                .build();

        FraudCase savedCase = fraudCaseRepository.save(fraudCase);
        fraudCaseAuditService.recordAudit(savedCase, FraudCaseAuditEventType.CASE_CREATED, null, caseStatus.name(), "SYSTEM");

        log.info("Created FraudCase ID={} with status={} priority={} for transaction ID={}",
                savedCase.getId(), caseStatus, priority, saved.getId());

        return toResponse(saved, balanceAfter, fraudReason);
    }

    /**
     * Returns all transactions for a given user, newest first.
     */
    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        return transactionRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(t -> toResponse(t, null, null))
                .collect(Collectors.toList());
    }

    /**
     * Returns all flagged transactions (status = FLAGGED), newest first.
     * Used by the fraud admin monitoring endpoint.
     */
    @Transactional(readOnly = true)
    public List<TransactionResponse> getFlaggedTransactions() {
        return transactionRepository
                .findByStatusOrderByCreatedAtDesc(TransactionStatus.FLAGGED)
                .stream()
                .map(t -> toResponse(t, null, null))
                .collect(Collectors.toList());
    }

    /**
     * Returns a single transaction by its ID.
     */
    @Transactional(readOnly = true)
    public TransactionResponse getTransactionById(Long id) {
        Transaction txn = transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found with ID: " + id));
        return toResponse(txn, null, null);
    }

    private TransactionResponse toResponse(Transaction txn, BigDecimal newBalance, String fraudReason) {
        return TransactionResponse.builder()
                .id(txn.getId())
                .userId(txn.getUser().getId())
                .amount(txn.getAmount())
                .transactionType(txn.getTransactionType())
                .status(txn.getStatus())
                .fraudReason(fraudReason != null ? fraudReason : txn.getFraudReason())
                .newBalance(newBalance)
                .createdAt(txn.getCreatedAt())
                .build();
    }
}
