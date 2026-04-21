package com.fraudapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response payload for a single transaction.
 * {@code newBalance} and {@code fraudReason} are omitted when null.
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionResponse {

    private Long id;
    private Long userId;
    private BigDecimal amount;
    private String transactionType;

    /** APPROVED or FLAGGED */
    private String status;

    /** Present only when status = FLAGGED */
    private String fraudReason;

    /** Present only when status = APPROVED (reflects balance after transaction) */
    private BigDecimal newBalance;

    private LocalDateTime createdAt;
}
