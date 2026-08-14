package com.sms.repository;

import com.sms.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entity.
 * Provides data access methods for user authentication and authorization.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find a user by username.
     */
    Optional<User> findByUsername(String username);

    /**
     * Find a user by email.
     */
    Optional<User> findByEmail(String email);

    /**
     * Find a user by username or email.
     */
    @Query("SELECT u FROM User u WHERE u.username = :usernameOrEmail OR u.email = :usernameOrEmail")
    Optional<User> findByUsernameOrEmail(@Param("usernameOrEmail") String usernameOrEmail);

    /**
     * Find all active users.
     */
    Page<User> findByIsActiveTrue(Pageable pageable);

    /**
     * Find all users by role.
     */
    Page<User> findByRoleAndIsActiveTrue(String role, Pageable pageable);

    /**
     * Find a user by employee ID.
     */
    Optional<User> findByEmployeeId(Long employeeId);

    /**
     * Check if username exists.
     */
    boolean existsByUsername(String username);

    /**
     * Check if email exists.
     */
    boolean existsByEmail(String email);

    /**
     * Find locked accounts.
     */
    Page<User> findByAccountLockedTrue(Pageable pageable);
}
