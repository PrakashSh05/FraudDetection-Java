package com.fraudapi.service;

import com.fraudapi.constants.TransactionStatus;
import com.fraudapi.constants.TransactionType;
import com.fraudapi.dto.TransactionRequest;
import com.fraudapi.dto.TransactionResponse;
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
 * Core business logic for transaction processing.
 *
 * <p><b>Processing flow:</b>
 * <ol>
 *   <li>Load the user (throws 404 if absent)</li>
 *   <li>Run fraud checks via {@link FraudDetectionService}</li>
 *   <li>If FLAGGED — persist without touching balance</li>
 *   <li>If APPROVED DEBIT — validate balance, deduct, save</li>
 *   <li>If APPROVED CREDIT — add to balance, save</li>
 * </ol>
 *
 * <p>All steps run in a single {@code @Transactional} boundary with
 * {@code REPEATABLE_READ} isolation to prevent lost-update anomalies when two
 * concurrent requests read the same balance before either writes it back.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final FraudDetectionService fraudDetectionService;

    /**
     * Creates a transaction after performing balance validation and fraud checks.
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

        // Step 2 — fraud check
        String fraudReason = fraudDetectionService.checkFraud(request.getUserId(), amount);

        Transaction txn = Transaction.builder()
                .user(user)
                .amount(amount)
                .transactionType(request.getTransactionType())
                .build();

        BigDecimal balanceAfter = user.getBalance(); // will be updated if APPROVED

        if (fraudReason != null) {
            // Step 3 — FLAGGED: record the transaction without touching balance
            txn.setStatus(TransactionStatus.FLAGGED);
            txn.setFraudReason(fraudReason);
            log.warn("Transaction FLAGGED for userId={} reason='{}'", user.getId(), fraudReason);

        } else {
            // Step 4 — APPROVED: apply balance change
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
