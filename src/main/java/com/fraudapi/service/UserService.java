package com.fraudapi.service;

import com.fraudapi.dto.CreateUserRequest;
import com.fraudapi.dto.UserResponse;
import com.fraudapi.exception.DuplicateEmailException;
import com.fraudapi.exception.UserNotFoundException;
import com.fraudapi.model.User;
import com.fraudapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Business logic for user management.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    /**
     * Creates and persists a new user.
     *
     * @param request user creation payload
     * @return the persisted user as a response DTO
     * @throws DuplicateEmailException if the email is already registered
     */
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        log.info("Creating user with email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(request.getEmail());
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .balance(request.getBalance())
                .build();

        User saved = userRepository.save(user);
        log.info("User created successfully with id: {}", saved.getId());

        return toResponse(saved);
    }

    /**
     * Retrieves a user by ID.
     *
     * @param userId user identifier
     * @return the user as a response DTO
     * @throws UserNotFoundException if the user does not exist
     */
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {
        log.debug("Fetching user with id: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        return toResponse(user);
    }

    // ------------------------------------------------------------------ //
    //  Mapper                                                              //
    // ------------------------------------------------------------------ //

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .balance(user.getBalance())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
