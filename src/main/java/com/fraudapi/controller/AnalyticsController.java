package com.fraudapi.controller;

import com.fraudapi.dto.ApiResponse;
import com.fraudapi.dto.analytics.AnalyticsOverviewResponse;
import com.fraudapi.dto.analytics.DailyTrendResponse;
import com.fraudapi.dto.analytics.RiskDistributionResponse;
import com.fraudapi.dto.analytics.TopRuleResponse;
import com.fraudapi.service.AnalyticsService;
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
 * REST controller exposing read-only risk analytics endpoints for reporting and dashboards.
 *
 * <p>Base path: {@code /api/analytics}
 */
@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Analytics", description = "Transaction risk analytics resources for dashboards")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    /**
     * GET /api/analytics/overview — Returns high-level summary metrics.
     */
    @GetMapping("/overview")
    @Operation(
        summary = "Get analytics overview",
        description = "Returns total transactions, decision breakdown, and overall average risk score."
    )
    public ResponseEntity<ApiResponse<AnalyticsOverviewResponse>> getOverviewAnalytics() {
        log.info("GET /api/analytics/overview");
        AnalyticsOverviewResponse overview = analyticsService.getOverviewAnalytics();
        return ResponseEntity.ok(ApiResponse.success("Overview analytics retrieved successfully", overview));
    }

    /**
     * GET /api/analytics/risk-distribution — Returns transaction count per risk level tier.
     */
    @GetMapping("/risk-distribution")
    @Operation(
        summary = "Get risk distribution",
        description = "Returns transaction counts grouped by qualitative risk level tiers (LOW, MEDIUM, HIGH, CRITICAL)."
    )
    public ResponseEntity<ApiResponse<List<RiskDistributionResponse>>> getRiskDistributionAnalytics() {
        log.info("GET /api/analytics/risk-distribution");
        List<RiskDistributionResponse> distribution = analyticsService.getRiskDistributionAnalytics();
        return ResponseEntity.ok(ApiResponse.success("Risk distribution metrics retrieved successfully", distribution));
    }

    /**
     * GET /api/analytics/top-rules — Returns top triggered rules ordered by frequency.
     */
    @GetMapping("/top-rules")
    @Operation(
        summary = "Get top triggered rules",
        description = "Returns top triggered fraud rules sorted by execution frequency descending."
    )
    public ResponseEntity<ApiResponse<List<TopRuleResponse>>> getTopRulesAnalytics() {
        log.info("GET /api/analytics/top-rules");
        List<TopRuleResponse> topRules = analyticsService.getTopRulesAnalytics();
        return ResponseEntity.ok(ApiResponse.success("Top triggered rules retrieved successfully", topRules));
    }

    /**
     * GET /api/analytics/daily-trend — Returns daily volume and average risk score metrics.
     */
    @GetMapping("/daily-trend")
    @Operation(
        summary = "Get daily trend metrics",
        description = "Returns daily transaction volume and average risk score trends."
    )
    public ResponseEntity<ApiResponse<List<DailyTrendResponse>>> getDailyTrendAnalytics() {
        log.info("GET /api/analytics/daily-trend");
        List<DailyTrendResponse> dailyTrends = analyticsService.getDailyTrendAnalytics();
        return ResponseEntity.ok(ApiResponse.success("Daily trend metrics retrieved successfully", dailyTrends));
    }
}
