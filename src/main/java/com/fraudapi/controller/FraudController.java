package com.fraudapi.controller;

import com.fraudapi.dto.ApiResponse;
import com.fraudapi.dto.TransactionResponse;
import com.fraudapi.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for the fraud monitoring / admin dashboard.
 *
 * <p>Base path: {@code /api/fraud}
 */
@RestController
@RequestMapping("/api/fraud")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Fraud", description = "Fraud monitoring and flagged transaction retrieval")
public class FraudController {

    private final TransactionService transactionService;

    /**
     * GET /api/fraud/flagged — Returns all transactions flagged as suspicious.
     *
     * <p>Useful for an admin dashboard or audit trail. Returns newest flagged
     * transactions first.
     */
    @GetMapping("/flagged")
    @Operation(
        summary = "Get all flagged transactions",
        description = "Returns every transaction that was blocked by fraud detection, ordered by newest first."
    )
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getFlaggedTransactions() {
        log.info("GET /api/fraud/flagged");
        List<TransactionResponse> flagged = transactionService.getFlaggedTransactions();
        return ResponseEntity.ok(
                ApiResponse.success("Found " + flagged.size() + " flagged transaction(s)", flagged));
    }
}
