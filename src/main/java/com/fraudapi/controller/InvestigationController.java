package com.fraudapi.controller;

import com.fraudapi.dto.ApiResponse;
import com.fraudapi.dto.investigation.InvestigationResponse;
import com.fraudapi.service.InvestigationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing read-only investigation audit trail endpoints for administrators.
 *
 * <p>Base path: {@code /api/investigation}
 */
@RestController
@RequestMapping("/api/investigation")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Investigation", description = "Fraud investigation and audit trail resources")
public class InvestigationController {

    private final InvestigationService investigationService;

    /**
     * GET /api/investigation/transaction/{transactionId} — Returns full risk evaluation report for a transaction.
     */
    @GetMapping("/transaction/{transactionId}")
    @Operation(
        summary = "Get transaction investigation report",
        description = "Reconstructs the full fraud risk evaluation, score, decision, and triggered rules for a transaction."
    )
    public ResponseEntity<ApiResponse<InvestigationResponse>> getInvestigationReport(
            @PathVariable Long transactionId) {

        log.info("GET /api/investigation/transaction/{}", transactionId);
        InvestigationResponse report = investigationService.getInvestigationReport(transactionId);
        return ResponseEntity.ok(ApiResponse.success("Investigation report retrieved successfully", report));
    }
}
