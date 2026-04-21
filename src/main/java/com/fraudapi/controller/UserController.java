package com.fraudapi.controller;

import com.fraudapi.dto.ApiResponse;
import com.fraudapi.dto.CreateUserRequest;
import com.fraudapi.dto.UserResponse;
import com.fraudapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for user management endpoints.
 *
 * <p>Base path: {@code /api/users}
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Users", description = "User registration and retrieval")
public class UserController {

    private final UserService userService;

    /**
     * POST /api/users — Registers a new user with an initial balance.
     */
    @PostMapping
    @Operation(summary = "Create a new user", description = "Registers a user with name, email, and initial balance.")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody CreateUserRequest request) {

        log.info("POST /api/users — email={}", request.getEmail());
        UserResponse user = userService.createUser(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("User created successfully", user));
    }

    /**
     * GET /api/users/{id} — Retrieves a user by their ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        log.info("GET /api/users/{}", id);
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
}
