package com.fraudapi.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Request payload for creating a transaction.
 */
@Data
public class TransactionRequest {

    @NotNull(message = "userId is required")
    private Long userId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Amount must have at most 10 digits and 2 decimal places")
    private BigDecimal amount;

    @NotBlank(message = "Transaction type is required")
    @Pattern(regexp = "^(DEBIT|CREDIT)$", message = "Transaction type must be DEBIT or CREDIT")
    private String transactionType;
}
