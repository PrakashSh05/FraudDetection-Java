package com.fraudapi.dto.cases;

/**
 * Summary record response for fraud case queue statistics.
 */
public record FraudCaseQueueSummaryResponse(
        long totalCases,
        long openCases,
        long assignedCases,
        long underReviewCases,
        long approvedCases,
        long declinedCases,
        long escalatedCases,
        long closedCases,
        long criticalCases,
        long highCases,
        long mediumCases,
        long lowCases
) {}
