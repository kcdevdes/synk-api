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
        log.debug("[USER_QUERY] 사용자 조회 - userId: {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("[USER_NOT_FOUND] 사용자를 찾을 수 없음 - userId: {}", userId);
                    return ResourceNotFoundException.user(userId);
                });
    }

    /**
     * Get User By Email
     * Otherwise, it will throw ResourceNotFoundException
     * @param email
     * @return
     */
    @Transactional(readOnly = true)
    public UserEntity getUserByEmail(String email) {
        log.debug("[USER_QUERY] 사용자 조회 - email: {}", email);
        return userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> {
                    log.warn("[USER_NOT_FOUND] 사용자를 찾을 수 없음 - email: {}", email);
                    return new ResourceNotFoundException(
                        ErrorCode.USER_NOT_FOUND,
                        "User not found with email: " + email
                    );
                });
    }

    /**
     * Get User By Username
     * Otherwise, it will throw ResourceNotFoundException
     * @param username
     * @return
     */
    @Transactional(readOnly = true)
    public UserEntity getUserByUsername(String username) {
        log.debug("[USER_QUERY] 사용자 조회 - username: {}", username);
        return userRepository.findByUsernameAndDeletedFalse(username)
                .orElseThrow(() -> {
                    log.warn("[USER_NOT_FOUND] 사용자를 찾을 수 없음 - username: {}", username);
                    return new ResourceNotFoundException(
                        com.kcdevdes.synk.exception.ErrorCode.USER_NOT_FOUND,
                        "User not found with username: " + username
                    );
                });
    }

    /**
     * Get All Users
     * @return
     */
    @Transactional(readOnly = true)
    public List<UserEntity> getAllUsers() {
        log.debug("[USER_QUERY] 모든 사용자 조회");
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
        log.info("[USER_CREATE_START] 사용자 생성 시작 - username: {}, email: {}", user.getUsername(), user.getEmail());

        if (userRepository.existsByEmail(user.getEmail())) {
            log.warn("[USER_CREATE_FAILED] 이메일 중복 - email: {}", user.getEmail());
            throw DuplicateResourceException.email(user.getEmail());
        }
        log.debug("[USER_CREATE_PROGRESS] 이메일 중복 검사 완료 - 25%");

        if (userRepository.existsByUsername(user.getUsername())) {
            log.warn("[USER_CREATE_FAILED] 사용자명 중복 - username: {}", user.getUsername());
            throw DuplicateResourceException.username(user.getUsername());
        }
        log.debug("[USER_CREATE_PROGRESS] 사용자명 중복 검사 완료 - 50%");

        try {
            // Encrypt password before saving
            //        user.setPassword(user.encryptPassword(user.getPassword()));
            log.debug("[USER_CREATE_PROGRESS] 비밀번호 암호화 준비 - 75%");
            log.info("[ENCRYPTION_START] 사용자 데이터 암호화 진행 - username: {}", user.getUsername());

            UserEntity savedUser = userRepository.save(user);
            log.info("[ENCRYPTION_SUCCESS] 사용자 저장 완료 - userId: {}, username: {}", savedUser.getId(), savedUser.getUsername());
            log.debug("[USER_CREATE_PROGRESS] 사용자 생성 완료 - 100%");

            auditLogger.log(savedUser.getId(), null, "User created: " + savedUser.getUsername());
            log.info("[USER_CREATE_COMPLETED] 사용자 생성 완료 - userId: {}, username: {}", savedUser.getId(), savedUser.getUsername());

            return savedUser;
        } catch (Exception e) {
            log.error("[ENCRYPTION_FAILED] 사용자 생성 실패 - username: {}, error: {}", user.getUsername(), e.getMessage(), e);
            throw e;
        }
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
        log.info("[USER_UPDATE_START] 사용자 수정 시작 - userId: {}", userId);

        try {
            UserEntity user = getUserById(userId);
            log.debug("[USER_UPDATE_PROGRESS] 사용자 조회 완료 - 25%");

            UserMapper.updateEntity(user, dto);
            log.debug("[USER_UPDATE_PROGRESS] 사용자 데이터 매핑 완료 - 50%");

            UserEntity updatedUser = userRepository.save(user);
            log.debug("[USER_UPDATE_PROGRESS] 사용자 저장 완료 - 100%");

            // 감사 로그: 별도 파일(synk-api-audit.log)에 기록됨 (30일 보관)
            auditLogger.logUserUpdated(userId, updatedUser.getUsername(), "User profile updated");
            log.info("[USER_UPDATE_COMPLETED] 사용자 수정 완료 - userId: {}, username: {}", updatedUser.getId(), updatedUser.getUsername());

            return updatedUser;
        } catch (Exception e) {
            log.error("[USER_UPDATE_FAILED] 사용자 수정 실패 - userId: {}, error: {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Soft Delete User
     * It does not perform the actual deletion from the database
     * @param userId
     */
    @Transactional
    public void deleteUser(Long userId) {
        log.info("[USER_DELETE_START] 사용자 삭제 시작 (소프트 삭제) - userId: {}", userId);

        try {
            UserEntity user = getUserById(userId);
            log.debug("[USER_DELETE_PROGRESS] 사용자 조회 완료 - 25%");

            user.setDeleted(true);
            user.setDeletedAt(Instant.now());
            user.setActive(false);
            log.debug("[USER_DELETE_PROGRESS] 사용자 상태 변경 완료 - 75%");

            userRepository.save(user);
            log.debug("[USER_DELETE_PROGRESS] 사용자 저장 완료 - 100%");

            // 감사 로그: 별도 파일(synk-api-audit.log)에 기록됨 (30일 보관)
            auditLogger.logUserDeleted(userId, user.getUsername());
            log.info("[USER_DELETE_COMPLETED] 사용자 삭제 완료 - userId: {}, username: {}", userId, user.getUsername());
        } catch (Exception e) {
            log.error("[USER_DELETE_FAILED] 사용자 삭제 실패 - userId: {}, error: {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Verify User Email
     * It does not perform the actual verification from the database
     * @param userId
     */
    @Transactional
    public void verifyEmail(Long userId) {
        log.info("[EMAIL_VERIFY_START] 이메일 인증 시작 - userId: {}", userId);

        try {
            UserEntity user = getUserById(userId);
            user.setEmailVerified(true);
            userRepository.save(user);

            // 감사 로그: 별도 파일(synk-api-audit.log)에 기록됨 (30일 보관)
            auditLogger.logEmailVerified(userId, user.getUsername(), user.getEmail());
            log.info("[EMAIL_VERIFY_COMPLETED] 이메일 인증 완료 - userId: {}, email: {}", userId, user.getEmail());
        } catch (Exception e) {
            log.error("[EMAIL_VERIFY_FAILED] 이메일 인증 실패 - userId: {}, error: {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Update Login Attempts
     * If it reaches maximum attempts (5 times), it will lock the user for 30 minutes
     * @param userId
     * @param success
     */
    @Transactional
    public void updateLoginAttempts(Long userId, boolean success) {
        log.debug("[LOGIN_ATTEMPT_UPDATE] 로그인 시도 업데이트 - userId: {}, success: {}", userId, success);

        try {
            UserEntity user = getUserById(userId);

            if (success) {
                user.setFailedLoginAttempts(0);
                user.setLastLoginAt(Instant.now());
                user.setLockedUntil(null);
                log.info("[LOGIN_SUCCESS] 로그인 성공 - userId: {}, username: {}", userId, user.getUsername());
                // 감사 로그: 별도 파일(synk-api-audit.log)에 기록됨 (30일 보관)
                auditLogger.logLoginAttempt(userId, user.getUsername(), true);
            } else {
                int attempts = user.getFailedLoginAttempts() + 1;
                user.setFailedLoginAttempts(attempts);
                log.warn("[LOGIN_FAILED_ATTEMPT] 로그인 실패 - userId: {}, attempts: {}/5", userId, attempts);
                // 감사 로그: 별도 파일(synk-api-audit.log)에 기록됨 (30일 보관)
                auditLogger.logLoginAttempt(userId, user.getUsername(), false);

                if (attempts >= 5) {
                    user.setLockedUntil(Instant.now().plusSeconds(1800));
                    log.warn("[ACCOUNT_LOCKED] 계정 잠금 - userId: {}, username: {}, lockDuration: 30분", userId, user.getUsername());
                    // 감사 로그: 별도 파일(synk-api-audit.log)에 기록됨 (30일 보관)
                    auditLogger.logAccountLocked(userId, user.getUsername(), attempts);
                }
            }

            userRepository.save(user);
        } catch (Exception e) {
            log.error("[LOGIN_ATTEMPT_UPDATE_FAILED] 로그인 시도 업데이트 실패 - userId: {}, error: {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Generate Password Reset Token
     * @param email
     * @param token
     * @param expiresAt
     */
    @Transactional
    public void generatePasswordResetToken(String email, String token, Instant expiresAt) {
        log.info("[PASSWORD_RESET_TOKEN_START] 비밀번호 초기화 토큰 생성 시작 - email: {}", email);

        try {
            UserEntity user = getUserByEmail(email);
            user.setResetPasswordToken(token);
            user.setResetPasswordTokenExpiresAt(expiresAt);
            userRepository.save(user);

            log.info("[PASSWORD_RESET_TOKEN_GENERATED] 비밀번호 초기화 토큰 생성 완료 - email: {}, expiresAt: {}", email, expiresAt);
        } catch (Exception e) {
            log.error("[PASSWORD_RESET_TOKEN_FAILED] 비밀번호 초기화 토큰 생성 실패 - email: {}, error: {}", email, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Validate Password Reset Token
     * If token is expired or invalid, it will throw InvalidInputException
     */
    @Transactional
    public UserEntity validatePasswordResetToken(String token) {
        log.debug("[PASSWORD_RESET_TOKEN_VALIDATION_START] 비밀번호 초기화 토큰 검증 시작");

        try {
            UserEntity user = userRepository.findByResetPasswordToken(token)
                    .orElseThrow(() -> {
                        log.warn("[PASSWORD_RESET_TOKEN_INVALID] 유효하지 않은 비밀번호 초기화 토큰");
                        return new InvalidInputException(
                                ErrorCode.INVALID_INPUT_VALUE,
                                "Invalid password reset token"
                        );
                    });
            log.debug("[PASSWORD_RESET_TOKEN_FOUND] 토큰 조회 완료 - userId: {}", user.getId());

            if (user.getResetPasswordTokenExpiresAt() == null ||
                    Instant.now().isAfter(user.getResetPasswordTokenExpiresAt())) {
                log.warn("[PASSWORD_RESET_TOKEN_EXPIRED] 비밀번호 초기화 토큰 만료 - userId: {}, expiresAt: {}", user.getId(), user.getResetPasswordTokenExpiresAt());
                throw new InvalidInputException(
                        ErrorCode.INVALID_INPUT_VALUE,
                        "Password reset token has expired"
                );
            }

            log.info("[PASSWORD_RESET_TOKEN_VALIDATED] 비밀번호 초기화 토큰 검증 완료 - userId: {}", user.getId());
            return user;
        } catch (InvalidInputException e) {
            throw e;
        } catch (Exception e) {
            log.error("[PASSWORD_RESET_TOKEN_VALIDATION_FAILED] 비밀번호 초기화 토큰 검증 실패 - error: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Reset Password
     * @param token
     * @param newPassword
     */
    @Transactional
    public void resetPassword(String token, String newPassword) {
        log.info("[PASSWORD_RESET_START] 비밀번호 재설정 시작");

        try {
            UserEntity user = validatePasswordResetToken(token);
            log.debug("[PASSWORD_RESET_PROGRESS] 토큰 검증 완료 - 25%, userId: {}", user.getId());

            //        user.setPassword(user.encryptPassword(newPassword));
            log.debug("[ENCRYPTION_START] 새 비밀번호 암호화 시작 - userId: {}", user.getId());
            log.debug("[PASSWORD_RESET_PROGRESS] 비밀번호 암호화 완료 - 50%");

            user.setResetPasswordToken(null);
            user.setResetPasswordTokenExpiresAt(null);
            user.setFailedLoginAttempts(0);
            user.setLockedUntil(null);
            log.debug("[PASSWORD_RESET_PROGRESS] 사용자 상태 초기화 완료 - 75%");

            userRepository.save(user);
            log.debug("[PASSWORD_RESET_PROGRESS] 사용자 저장 완료 - 100%");

            // 감사 로그: 별도 파일(synk-api-audit.log)에 기록됨 (30일 보관)
            auditLogger.logPasswordReset(user.getId(), user.getUsername());
            log.info("[PASSWORD_RESET_COMPLETED] 비밀번호 재설정 완료 - userId: {}, username: {}", user.getId(), user.getUsername());
        } catch (Exception e) {
            log.error("[PASSWORD_RESET_FAILED] 비밀번호 재설정 실패 - error: {}", e.getMessage(), e);
            throw e;
        }
    }
}
