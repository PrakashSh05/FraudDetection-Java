package com.fraudapi.dto.analytics;

/**
 * Transaction count aggregate for a specific risk level tier.
 */
public record RiskDistributionResponse(
        String riskLevel,
        long count
) {}
