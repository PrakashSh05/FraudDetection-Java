package com.fraudapi.dto.analytics;

/**
 * High-level summary metrics response for overall transaction risk evaluation.
 */
public record AnalyticsOverviewResponse(
        long totalTransactions,
        long approvedTransactions,
        long monitorTransactions,
        long reviewTransactions,
        long rejectedTransactions,
        double averageRiskScore
) {}
