package com.fraudapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a DEBIT transaction exceeds the user's current balance.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InsufficientBalanceException extends RuntimeException {

    public InsufficientBalanceException(String currentBalance, String requestedAmount) {
        super(String.format(
                "Insufficient balance. Current balance: ₹%s, Requested amount: ₹%s",
                currentBalance, requestedAmount));
    }
}
