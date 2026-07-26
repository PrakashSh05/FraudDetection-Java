package com.fraudapi.dto.analytics;

/**
 * Daily aggregated transaction volume and average risk score metrics.
 */
public record DailyTrendResponse(
        String date,
        long transactions,
        double averageRiskScore
) {}
