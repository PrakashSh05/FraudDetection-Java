package com.fraudapi.dto.cases;

import com.fraudapi.constants.FraudCaseStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request payload for finalizing and resolving a fraud case.
 */
public record ResolveCaseRequest(
        @NotBlank(message = "resolution is required")
        String resolution,

        @NotNull(message = "status is required")
        FraudCaseStatus status
) {}
