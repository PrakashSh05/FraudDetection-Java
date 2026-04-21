package com.fraudapi.service;

import com.fraudapi.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Evaluates transactions against configurable fraud rules.
 *
 * <p><b>Rules implemented:</b>
 * <ol>
 *   <li><b>High-Amount Rule</b> — amount &gt; {@code fraud.rules.high-amount-threshold} (default ₹50,000)</li>
 *   <li><b>Velocity Rule</b> — more than {@code fraud.rules.max-transactions-per-window} transactions
 *       within {@code fraud.rules.time-window-minutes} minutes</li>
 * </ol>
 *
 * <p>Thresholds are externalised in {@code application.yml} so they can be adjusted without
 * redeployment — a key design point for interview discussions.
 *
 * <p><b>Strategy Pattern note:</b> Each rule is a discrete check. In a production system,
 * these would be extracted into {@code FraudRule} strategy implementations, enabling
 * rules to be toggled independently and new rules to be added without modifying this class.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FraudDetectionService {

    private final TransactionRepository transactionRepository;

    /** Maximum single-transaction amount before flagging. */
    @Value("${fraud.rules.high-amount-threshold}")
    private double highAmountThreshold;

    /** Maximum number of transactions allowed within the time window. */
    @Value("${fraud.rules.max-transactions-per-window}")
    private int maxTransactionsPerWindow;

    /** Length of the velocity-check window in minutes. */
    @Value("${fraud.rules.time-window-minutes}")
    private int timeWindowMinutes;

    /**
     * Evaluates all fraud rules for a given transaction attempt.
     *
     * @param userId the ID of the user making the transaction
     * @param amount the transaction amount
     * @return a human-readable fraud reason if flagged, {@code null} if the transaction is clean
     */
    public String checkFraud(Long userId, BigDecimal amount) {
        log.debug("Running fraud checks for userId={}, amount={}", userId, amount);

        // ── Rule 1: High-amount check ─────────────────────────────────── //
        if (amount.compareTo(BigDecimal.valueOf(highAmountThreshold)) > 0) {
            String reason = String.format(
                    "Amount ₹%.2f exceeds the allowed limit of ₹%.2f",
                    amount.doubleValue(), highAmountThreshold);
            log.warn("Fraud rule TRIGGERED [high-amount] userId={} amount={}", userId, amount);
            return reason;
        }

        // ── Rule 2: Velocity check (transaction frequency) ───────────── //
        LocalDateTime windowStart = LocalDateTime.now().minusMinutes(timeWindowMinutes);
        long recentCount = transactionRepository.countRecentTransactions(userId, windowStart);

        if (recentCount >= maxTransactionsPerWindow) {
            String reason = String.format(
                    "Too many transactions: %d in the last %d minutes (limit: %d)",
                    recentCount, timeWindowMinutes, maxTransactionsPerWindow);
            log.warn("Fraud rule TRIGGERED [velocity] userId={} recentCount={}", userId, recentCount);
            return reason;
        }

        log.debug("No fraud detected for userId={}", userId);
        return null; // Clean — no fraud detected
    }
}
