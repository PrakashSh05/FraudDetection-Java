package com.fraudapi.dto.cases;

import com.fraudapi.constants.FraudCaseStatus;
import jakarta.validation.constraints.NotNull;

/**
 * Request payload for updating the status of a fraud case.
 */
public record UpdateCaseStatusRequest(
        @NotNull(message = "status is required")
        FraudCaseStatus status
) {}
