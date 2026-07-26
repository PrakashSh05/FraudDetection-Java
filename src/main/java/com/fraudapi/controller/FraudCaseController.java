package com.fraudapi.controller;

import com.fraudapi.constants.FraudCasePriority;
import com.fraudapi.constants.FraudCaseStatus;
import com.fraudapi.dto.ApiResponse;
import com.fraudapi.dto.cases.*;
import com.fraudapi.service.FraudCaseAuditService;
import com.fraudapi.service.FraudCaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller exposing endpoints for compliance analysts to manage fraud case lifecycles,
 * execute dynamic queue filtering, fetch queue statistics, and view audit timelines.
 *
 * <p>Base path: {@code /api/cases}
 */
@RestController
@RequestMapping("/api/cases")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Cases", description = "Fraud Case Management workflow, queue filtering, statistics, and audit resources")
public class FraudCaseController {

    private final FraudCaseService fraudCaseService;
    private final FraudCaseAuditService fraudCaseAuditService;

    /**
     * GET /api/cases — Returns paginated and dynamically filtered fraud case queue.
     */
    @GetMapping
    @Operation(
        summary = "Get filtered fraud case queue",
        description = "Returns a paginated list of fraud cases supporting dynamic filtering by status, priority, assignedTo, transactionId, riskLevel, and caseId."
    )
    public ResponseEntity<ApiResponse<Page<FraudCaseSummaryResponse>>> getCaseQueue(
            @RequestParam(required = false) FraudCaseStatus status,
            @RequestParam(required = false) FraudCasePriority priority,
            @RequestParam(required = false) String assignedTo,
            @RequestParam(required = false) Long transactionId,
            @RequestParam(required = false) String riskLevel,
            @RequestParam(required = false) Long caseId,
            @PageableDefault(sort = "openedAt", direction = Sort.Direction.DESC) Pageable pageable) {

        log.info("GET /api/cases status={} priority={} assignedTo={} transactionId={} riskLevel={} caseId={} page={} size={}",
                status, priority, assignedTo, transactionId, riskLevel, caseId, pageable.getPageNumber(), pageable.getPageSize());

        Page<FraudCaseSummaryResponse> queue = fraudCaseService.getCaseQueue(
                status, priority, assignedTo, transactionId, riskLevel, caseId, pageable);

        return ResponseEntity.ok(ApiResponse.success("Fraud case queue retrieved successfully", queue));
    }

    /**
     * GET /api/cases/summary — Returns aggregated fraud case queue statistics.
     */
    @GetMapping("/summary")
    @Operation(
        summary = "Get fraud case queue summary statistics",
        description = "Returns breakdown counts of fraud cases grouped by status (OPEN, ASSIGNED, UNDER_REVIEW, etc.) and priority (CRITICAL, HIGH, etc.)."
    )
    public ResponseEntity<ApiResponse<FraudCaseQueueSummaryResponse>> getQueueSummary() {
        log.info("GET /api/cases/summary");
        FraudCaseQueueSummaryResponse summary = fraudCaseService.getQueueSummary();
        return ResponseEntity.ok(ApiResponse.success("Fraud case queue summary retrieved successfully", summary));
    }

    /**
     * GET /api/cases/{caseId} — Returns complete case details and telemetry.
     */
    @GetMapping("/{caseId}")
    @Operation(
        summary = "Get fraud case details",
        description = "Returns complete case details including transaction summary, risk evaluation, and triggered rules."
    )
    public ResponseEntity<ApiResponse<FraudCaseDetailResponse>> getCaseDetails(@PathVariable Long caseId) {
        log.info("GET /api/cases/{}", caseId);
        FraudCaseDetailResponse details = fraudCaseService.getCaseDetails(caseId);
        return ResponseEntity.ok(ApiResponse.success("Case details retrieved successfully", details));
    }

    /**
     * GET /api/cases/{caseId}/timeline — Returns ordered immutable audit timeline.
     */
    @GetMapping("/{caseId}/timeline")
    @Operation(
        summary = "Get fraud case audit timeline",
        description = "Returns the immutable audit log timeline for a case ordered by timestamp ascending."
    )
    public ResponseEntity<ApiResponse<List<FraudCaseAuditResponse>>> getCaseTimeline(@PathVariable Long caseId) {
        log.info("GET /api/cases/{}/timeline", caseId);
        List<FraudCaseAuditResponse> timeline = fraudCaseAuditService.getCaseTimeline(caseId);
        return ResponseEntity.ok(ApiResponse.success("Case audit timeline retrieved successfully", timeline));
    }

    /**
     * PATCH /api/cases/{caseId}/assign — Assigns case to an analyst.
     */
    @PatchMapping("/{caseId}/assign")
    @Operation(
        summary = "Assign fraud case to analyst",
        description = "Assigns an open fraud case to an analyst username/ID and transitions state to ASSIGNED."
    )
    public ResponseEntity<ApiResponse<FraudCaseDetailResponse>> assignCase(
            @PathVariable Long caseId,
            @Valid @RequestBody AssignCaseRequest request) {

        log.info("PATCH /api/cases/{}/assign analyst='{}'", caseId, request.assignedTo());
        FraudCaseDetailResponse updated = fraudCaseService.assignCase(caseId, request.assignedTo());
        return ResponseEntity.ok(ApiResponse.success("Case assigned successfully", updated));
    }

    /**
     * PATCH /api/cases/{caseId}/status — Updates case workflow status.
     */
    @PatchMapping("/{caseId}/status")
    @Operation(
        summary = "Update fraud case status",
        description = "Transitions case lifecycle status adhering to workflow state machine validation rules."
    )
    public ResponseEntity<ApiResponse<FraudCaseDetailResponse>> updateCaseStatus(
            @PathVariable Long caseId,
            @Valid @RequestBody UpdateCaseStatusRequest request) {

        log.info("PATCH /api/cases/{}/status status={}", caseId, request.status());
        FraudCaseDetailResponse updated = fraudCaseService.updateCaseStatus(caseId, request.status());
        return ResponseEntity.ok(ApiResponse.success("Case status updated successfully", updated));
    }

    /**
     * PATCH /api/cases/{caseId}/notes — Updates analyst review notes.
     */
    @PatchMapping("/{caseId}/notes")
    @Operation(
        summary = "Update fraud case review notes",
        description = "Appends or updates review notes recorded by a compliance analyst."
    )
    public ResponseEntity<ApiResponse<FraudCaseDetailResponse>> updateCaseNotes(
            @PathVariable Long caseId,
            @Valid @RequestBody UpdateCaseNotesRequest request) {

        log.info("PATCH /api/cases/{}/notes", caseId);
        FraudCaseDetailResponse updated = fraudCaseService.updateCaseNotes(caseId, request.reviewNotes());
        return ResponseEntity.ok(ApiResponse.success("Case notes updated successfully", updated));
    }

    /**
     * PATCH /api/cases/{caseId}/resolve — Resolves and finalizes a fraud case.
     */
    @PatchMapping("/{caseId}/resolve")
    @Operation(
        summary = "Resolve and finalize fraud case",
        description = "Finalizes a case with an approval/decline/escalation resolution and sets closedAt timestamp."
    )
    public ResponseEntity<ApiResponse<FraudCaseDetailResponse>> resolveCase(
            @PathVariable Long caseId,
            @Valid @RequestBody ResolveCaseRequest request) {

        log.info("PATCH /api/cases/{}/resolve status={} resolution='{}'", caseId, request.status(), request.resolution());
        FraudCaseDetailResponse updated = fraudCaseService.resolveCase(caseId, request.resolution(), request.status());
        return ResponseEntity.ok(ApiResponse.success("Case resolved successfully", updated));
    }
}
