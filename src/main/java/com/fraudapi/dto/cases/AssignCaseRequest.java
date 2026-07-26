package com.fraudapi.dto.cases;

import jakarta.validation.constraints.NotBlank;

/**
 * Request payload for assigning a fraud case to an analyst.
 */
public record AssignCaseRequest(
        @NotBlank(message = "assignedTo is required")
        String assignedTo
) {}
