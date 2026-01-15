package com.kcdevdes.synk.service;

import com.kcdevdes.synk.entity.UserEntity;
import com.kcdevdes.synk.exception.ErrorCode;
import com.kcdevdes.synk.exception.custom.DuplicateResourceException;
import com.kcdevdes.synk.exception.custom.InvalidInputException;
import com.kcdevdes.synk.exception.custom.ResourceNotFoundException;
import com.kcdevdes.synk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserEntity getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.user(userId));
    }

    @Transactional(readOnly = true)
    public UserEntity getUserByEmail(String email) {
        return userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        com.kcdevdes.synk.exception.ErrorCode.USER_NOT_FOUND,
                        "User not found with email: " + email
                ));
    }

    @Transactional(readOnly = true)
    public UserEntity getUserByUsername(String username) {
        return userRepository.findByUsernameAndDeletedFalse(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        com.kcdevdes.synk.exception.ErrorCode.USER_NOT_FOUND,
                        "User not found with username: " + username
                ));
    }

    @Transactional(readOnly = true)
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public UserEntity createUser(UserEntity user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw DuplicateResourceException.email(user.getEmail());
        }

        if (userRepository.existsByUsername(user.getUsername())) {
            throw DuplicateResourceException.username(user.getUsername());
        }

        return userRepository.save(user);
    }

    @Transactional
    public UserEntity updateUser(Long userId, UserEntity userDetails) {
        UserEntity user = getUserById(userId);

        if (userDetails.getEmail() != null && !userDetails.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(userDetails.getEmail())) {
                throw DuplicateResourceException.email(userDetails.getEmail());
            }
            user.setEmail(userDetails.getEmail());
        }

        if (userDetails.getUsername() != null && !userDetails.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(userDetails.getUsername())) {
                throw DuplicateResourceException.username(userDetails.getUsername());
            }
            user.setUsername(userDetails.getUsername());
        }

        if (userDetails.getFirstName() != null) {
            user.setFirstName(userDetails.getFirstName());
        }

        if (userDetails.getLastName() != null) {
            user.setLastName(userDetails.getLastName());
        }

        if (userDetails.getMobile() != null) {
            user.setMobile(userDetails.getMobile());
        }

        if (userDetails.getDefaultCurrency() != null) {
            user.setDefaultCurrency(userDetails.getDefaultCurrency());
        }

        if (userDetails.getLocale() != null) {
            user.setLocale(userDetails.getLocale());
        }

        if (userDetails.getTimezone() != null) {
            user.setTimezone(userDetails.getTimezone());
        }

        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        UserEntity user = getUserById(userId);
        user.setDeleted(true);
        user.setDeletedAt(Instant.now());
        user.setActive(false);
        userRepository.save(user);
    }

    @Transactional
    public void verifyEmail(Long userId) {
        UserEntity user = getUserById(userId);
        user.setEmailVerified(true);
        userRepository.save(user);
    }

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

    @Transactional
    public void generatePasswordResetToken(String email, String token, Instant expiresAt) {
        UserEntity user = getUserByEmail(email);
        user.setResetPasswordToken(token);
        user.setResetPasswordTokenExpiresAt(expiresAt);
        userRepository.save(user);
    }

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

    @Transactional
    public void resetPassword(String token, String newPassword) {
        UserEntity user = validatePasswordResetToken(token);
        user.setPassword(newPassword);
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiresAt(null);
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        userRepository.save(user);
    }
}
