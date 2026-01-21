package com.kcdevdes.synk.service;

import com.kcdevdes.synk.dto.request.UserUpdateDTO;
import com.kcdevdes.synk.entity.UserEntity;
import com.kcdevdes.synk.exception.ErrorCode;
import com.kcdevdes.synk.exception.custom.DuplicateResourceException;
import com.kcdevdes.synk.exception.custom.InvalidInputException;
import com.kcdevdes.synk.exception.custom.ResourceNotFoundException;
import com.kcdevdes.synk.mapper.UserMapper;
import com.kcdevdes.synk.repository.UserRepository;
import com.kcdevdes.synk.logger.AuditLogger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final AuditLogger auditLogger;

    /**
     * Get User By Id
     * Otherwise, it will throw ResourceNotFoundException
     * @param userId
     * @return
     */
    @Transactional(readOnly = true)
    public UserEntity getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.user(userId));
    }

    /**
     * Get User By Email
     * Otherwise, it will throw ResourceNotFoundException
     * @param email
     * @return
     */
    @Transactional(readOnly = true)
    public UserEntity getUserByEmail(String email) {
        return userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.USER_NOT_FOUND,
                        "User not found with email: " + email
                ));
    }

    /**
     * Get User By Username
     * Otherwise, it will throw ResourceNotFoundException
     * @param username
     * @return
     */
    @Transactional(readOnly = true)
    public UserEntity getUserByUsername(String username) {
        return userRepository.findByUsernameAndDeletedFalse(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        com.kcdevdes.synk.exception.ErrorCode.USER_NOT_FOUND,
                        "User not found with username: " + username
                ));
    }

    /**
     * Get All Users
     * @return
     */
    @Transactional(readOnly = true)
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Create a new user record by the given user details
     * If email or username already exists, it will throw DuplicateResourceException
     * @param user
     * @return
     */
    @Transactional
    public UserEntity createUser(UserEntity user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw DuplicateResourceException.email(user.getEmail());
        }

        if (userRepository.existsByUsername(user.getUsername())) {
            throw DuplicateResourceException.username(user.getUsername());
        }

        // Encrypt password before saving
//        user.setPassword(user.encryptPassword(user.getPassword()));
        log.info("Creating new user with username: {}", user.getUsername());
        auditLogger.logTransactionCreated(user.getId(), null, "User created: " + user.getUsername());
        return userRepository.save(user);
    }

    /**
     * Update user details by the given user details
     * If email or username already exists, it will throw DuplicateResourceException
     * @param userId
     * @param dto
     * @return
     */
    @Transactional
    public UserEntity updateUser(Long userId, UserUpdateDTO dto) {
        UserEntity user = getUserById(userId);
        UserMapper.updateEntity(user, dto);
        return userRepository.save(user);
    }

    /**
     * Soft Delete User
     * It does not perform the actual deletion from the database
     * @param userId
     */
    @Transactional
    public void deleteUser(Long userId) {
        UserEntity user = getUserById(userId);
        user.setDeleted(true);
        user.setDeletedAt(Instant.now());
        user.setActive(false);
        userRepository.save(user);
    }

    /**
     * Verify User Email
     * It does not perform the actual verification from the database
     * @param userId
     */
    @Transactional
    public void verifyEmail(Long userId) {
        UserEntity user = getUserById(userId);
        user.setEmailVerified(true);
        userRepository.save(user);
    }

    /**
     * Update Login Attempts
     * If it reaches maximum attempts (5 times), it will lock the user for 30 minutes
     * @param userId
     * @param success
     */
    @Transactional
    public void updateLoginAttempts(Long userId, boolean success) {
        UserEntity user = getUserById(userId);

        if (success) {
            user.setFailedLoginAttempts(0);
            user.setLastLoginAt(Instant.now());
            user.setLockedUntil(null);
        } else {
            int attempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(attempts);

            if (attempts >= 5) {
                user.setLockedUntil(Instant.now().plusSeconds(1800));
            }
        }

        userRepository.save(user);
    }

    /**
     * Generate Password Reset Token
     * @param email
     * @param token
     * @param expiresAt
     */
    @Transactional
    public void generatePasswordResetToken(String email, String token, Instant expiresAt) {
        UserEntity user = getUserByEmail(email);
        user.setResetPasswordToken(token);
        user.setResetPasswordTokenExpiresAt(expiresAt);
        userRepository.save(user);
    }

    /**
     * Validate Password Reset Token
     * If token is expired or invalid, it will throw InvalidInputException
     */
    @Transactional
    public UserEntity validatePasswordResetToken(String token) {
        UserEntity user = userRepository.findByResetPasswordToken(token)
                .orElseThrow(() -> new InvalidInputException(
                        ErrorCode.INVALID_INPUT_VALUE,
                        "Invalid password reset token"
                ));

        if (user.getResetPasswordTokenExpiresAt() == null ||
                Instant.now().isAfter(user.getResetPasswordTokenExpiresAt())) {
            throw new InvalidInputException(
                    ErrorCode.INVALID_INPUT_VALUE,
                    "Password reset token has expired"
            );
        }

        return user;
    }

    /**
     * Reset Password
     * @param token
     * @param newPassword
     */
    @Transactional
    public void resetPassword(String token, String newPassword) {
        UserEntity user = validatePasswordResetToken(token);
//        user.setPassword(user.encryptPassword(newPassword));
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiresAt(null);
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        userRepository.save(user);
    }
}
