package com.fraudapi.dto.cases;

import jakarta.validation.constraints.NotBlank;

/**
 * Request payload for appending review notes to a fraud case.
 */
public record UpdateCaseNotesRequest(
        @NotBlank(message = "reviewNotes is required")
        String reviewNotes
) {}
