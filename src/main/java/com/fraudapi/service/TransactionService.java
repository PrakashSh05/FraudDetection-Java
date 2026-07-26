package com.fraudapi.service;

import com.fraudapi.constants.Decision;
import com.fraudapi.constants.TransactionStatus;
import com.fraudapi.constants.TransactionType;
import com.fraudapi.dto.FraudDecision;
import com.fraudapi.dto.TransactionRequest;
import com.fraudapi.dto.TransactionResponse;
import com.fraudapi.dto.TriggeredRule;
import com.fraudapi.engine.TransactionContext;
import com.fraudapi.exception.InsufficientBalanceException;
import com.fraudapi.exception.TransactionNotFoundException;
import com.fraudapi.exception.UserNotFoundException;
import com.fraudapi.model.Transaction;
import com.fraudapi.model.User;
import com.fraudapi.repository.TransactionRepository;
import com.fraudapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Core business logic for transaction processing integrated with {@link TransactionRiskService}.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionRiskService transactionRiskService;

    /**
     * Creates a transaction after performing risk evaluation and balance validation.
     *
     * @param request the transaction payload
     * @return the persisted transaction as a response DTO
     * @throws UserNotFoundException        if the user ID is not found
     * @throws InsufficientBalanceException if DEBIT amount exceeds current balance
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TransactionResponse createTransaction(TransactionRequest request) {
        log.info("Processing transaction for userId={} type={} amount={}",
                request.getUserId(), request.getTransactionType(), request.getAmount());

        // Step 1 — load user
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException(request.getUserId()));

        BigDecimal amount = request.getAmount();

        // Step 2 — build transaction context & perform risk evaluation
        TransactionContext context = TransactionContext.builder()
                .user(user)
                .userId(user.getId())
                .amount(amount)
                .transactionType(request.getTransactionType())
                .build();

        FraudDecision fraudDecision = transactionRiskService.evaluateTransactionRisk(context);

        log.info("Risk telemetry for userId={}: score={}, level={}, decision={}, triggeredRules={}, duration={}ms",
                user.getId(), fraudDecision.getRiskScore(), fraudDecision.getRiskLevel(),
                fraudDecision.getDecision(),
                fraudDecision.getTriggeredRules() != null ? fraudDecision.getTriggeredRules().size() : 0,
                fraudDecision.getProcessingTimeMs());

        Transaction txn = Transaction.builder()
                .user(user)
                .amount(amount)
                .transactionType(request.getTransactionType())
                .build();

        BigDecimal balanceAfter = user.getBalance();
        String fraudReason = null;

        Decision decision = fraudDecision.getDecision();

        if (Decision.REJECTED.equals(decision) || Decision.REVIEW.equals(decision)) {
            // Step 3 — REJECTED / REVIEW: flag transaction without modifying balance
            txn.setStatus(TransactionStatus.FLAGGED);
            if (fraudDecision.getTriggeredRules() != null && !fraudDecision.getTriggeredRules().isEmpty()) {
                fraudReason = fraudDecision.getTriggeredRules().stream()
                        .map(TriggeredRule::getDescription)
                        .collect(Collectors.joining("; "));
            } else {
                fraudReason = fraudDecision.getSummary();
            }
            txn.setFraudReason(fraudReason);
            log.warn("Transaction FLAGGED for userId={} decision={} reason='{}'", user.getId(), decision, fraudReason);

        } else {
            // Step 4 — APPROVED / MONITOR: proceed with balance modification
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
     * Returns a single transaction by its ID.
     */
    @Transactional(readOnly = true)
    public TransactionResponse getTransactionById(Long id) {
        Transaction txn = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));
        return toResponse(txn, null, null);
    }

    /**
     * Returns all FLAGGED transactions (admin / fraud dashboard view).
     */
    @Transactional(readOnly = true)
    public List<TransactionResponse> getFlaggedTransactions() {
        return transactionRepository
                .findByStatusOrderByCreatedAtDesc(TransactionStatus.FLAGGED)
                .stream()
                .map(t -> toResponse(t, null, null))
                .collect(Collectors.toList());
    }

    // ------------------------------------------------------------------ //
    //  Mapper                                                              //
    // ------------------------------------------------------------------ //

    private TransactionResponse toResponse(Transaction txn,
                                            BigDecimal newBalance,
                                            String fraudReason) {
        boolean approved = TransactionStatus.APPROVED.equals(txn.getStatus());

        return TransactionResponse.builder()
                .id(txn.getId())
                .userId(txn.getUser().getId())
                .amount(txn.getAmount())
                .transactionType(txn.getTransactionType())
                .status(txn.getStatus())
                .fraudReason(txn.getFraudReason())
                .newBalance(approved ? newBalance : null)
                .createdAt(txn.getCreatedAt())
                .build();
    }
}
