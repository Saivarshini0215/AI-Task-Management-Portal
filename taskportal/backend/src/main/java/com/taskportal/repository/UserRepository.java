package com.taskportal.repository;

import com.taskportal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entity.
 * Provides database access methods for user management.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find a user by email (used for authentication).
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if a user with the given email already exists.
     */
    boolean existsByEmail(String email);
}
