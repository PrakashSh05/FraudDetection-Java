package com.fraudapi.repository;

import com.fraudapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Data-access layer for {@link User} entities.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Checks for duplicate email during user registration.
     */
    boolean existsByEmail(String email);

    /**
     * Lookup by email (useful for login / deduplication).
     */
    Optional<User> findByEmail(String email);
}
