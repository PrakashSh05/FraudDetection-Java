package com.fraudapi.dto.investigation;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Transaction details container for fraud investigation reports.
 */
public record TransactionDetails(
        Long transactionId,
        Long userId,
        BigDecimal amount,
        String transactionType,
        String status,
        LocalDateTime createdAt
) {}
