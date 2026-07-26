package com.fraudapi.service;

import com.fraudapi.constants.Decision;
import com.fraudapi.dto.FraudDecision;
import com.fraudapi.dto.TriggeredRule;
import com.fraudapi.engine.TransactionContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.stream.Collectors;

/**
 * Legacy fraud detection service.
 *
 * @deprecated Replaced by {@link TransactionRiskService} as part of Fraud Decision Platform V2.
 *             Maintained as a compatibility layer.
 */
@Deprecated
@Service
@RequiredArgsConstructor
@Slf4j
public class FraudDetectionService {

    private final TransactionRiskService transactionRiskService;

    /**
     * Legacy entry point delegating to {@link TransactionRiskService}.
     *
     * @param userId the ID of the user making the transaction
     * @param amount the transaction amount
     * @return a human-readable fraud reason if flagged/rejected, {@code null} if clean
     */
    public String checkFraud(Long userId, BigDecimal amount) {
        log.debug("Legacy checkFraud called for userId={}, delegating to TransactionRiskService", userId);

        TransactionContext context = TransactionContext.builder()
                .userId(userId)
                .amount(amount)
                .build();

        FraudDecision decision = transactionRiskService.evaluateTransactionRisk(context);

        if (Decision.REJECTED.equals(decision.getDecision()) || Decision.REVIEW.equals(decision.getDecision())) {
            if (decision.getTriggeredRules() != null && !decision.getTriggeredRules().isEmpty()) {
                return decision.getTriggeredRules().stream()
                        .map(TriggeredRule::getDescription)
                        .collect(Collectors.joining("; "));
            }
            return decision.getSummary();
        }

        return null;
    }
}
