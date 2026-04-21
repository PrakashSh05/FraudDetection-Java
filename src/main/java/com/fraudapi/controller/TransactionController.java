package com.fraudapi.controller;

import com.fraudapi.dto.ApiResponse;
import com.fraudapi.dto.TransactionRequest;
import com.fraudapi.dto.TransactionResponse;
import com.fraudapi.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for transaction processing endpoints.
 *
 * <p>Base path: {@code /api/transactions}
 */
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Transactions", description = "Transaction creation and history retrieval")
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * POST /api/transactions — Creates a transaction, auto-evaluating fraud rules.
     *
     * <p>If the transaction is FLAGGED, the balance is NOT deducted and the response
     * will include a {@code fraudReason} field explaining why.
     */
    @PostMapping
    @Operation(
        summary = "Create a transaction",
        description = "Submits a DEBIT or CREDIT transaction. Fraud checks run automatically. " +
                      "FLAGGED transactions do not affect the user's balance."
    )
    public ResponseEntity<ApiResponse<TransactionResponse>> createTransaction(
            @Valid @RequestBody TransactionRequest request) {

        log.info("POST /api/transactions userId={} type={} amount={}",
                request.getUserId(), request.getTransactionType(), request.getAmount());

        TransactionResponse response = transactionService.createTransaction(request);

        // Return 201 for approved, 200 for flagged (transaction recorded but not processed)
        HttpStatus status = "APPROVED".equals(response.getStatus())
                ? HttpStatus.CREATED
                : HttpStatus.OK;

        String message = "APPROVED".equals(response.getStatus())
                ? "Transaction approved"
                : "Transaction flagged as suspicious";

        return ResponseEntity.status(status).body(ApiResponse.success(message, response));
    }

    /**
     * GET /api/transactions/{id} — Retrieves a single transaction with its fraud status.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get transaction by ID")
    public ResponseEntity<ApiResponse<TransactionResponse>> getTransactionById(@PathVariable Long id) {
        log.info("GET /api/transactions/{}", id);
        return ResponseEntity.ok(ApiResponse.success(transactionService.getTransactionById(id)));
    }

    /**
     * GET /api/transactions/user/{userId} — Returns the full transaction history for a user.
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all transactions for a user", description = "Returns newest transactions first.")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactionsByUser(
            @PathVariable Long userId) {

        log.info("GET /api/transactions/user/{}", userId);
        List<TransactionResponse> transactions = transactionService.getTransactionsByUser(userId);
        return ResponseEntity.ok(
                ApiResponse.success("Found " + transactions.size() + " transaction(s)", transactions));
    }
}
