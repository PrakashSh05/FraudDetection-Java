package com.fraudapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response payload returned after creating or fetching a user.
 * Null fields are omitted from the JSON output.
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private BigDecimal balance;
    private LocalDateTime createdAt;
}
