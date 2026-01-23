package com.kcdevdes.synk.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 감사(Audit) 로깅을 담당하는 클래스
 *
 * 일반 로그(log)와의 차이:
 * - log: 애플리케이션 동작 추적 (DEBUG, INFO, WARN, ERROR)
 * - AuditLogger: 감사 기록 (보안, 컴플라이언스) - 별도 파일에 장기 보관
 *
 * 로그 위치:
 * - logs/synk-api.log (일반 로그, 7일 보관)
 * - logs/synk-api-audit.log (감시 로그, 30일 보관)
 */
@Component
public class AuditLogger {
    private static final Logger auditLog = LoggerFactory.getLogger("com.kcdevdes.synk.logger.audit");

    // ============ Transaction Audit Logs ============

    public void logTransactionCreated(Long transactionId, Long merchantId, String details) {
        auditLog.info("TRANSACTION_CREATED | transactionId={} | merchantId={} | details={}",
                transactionId, merchantId, details);
    }

    public void logTransactionUpdated(Long transactionId, Long merchantId, String changedFields) {
        auditLog.info("TRANSACTION_UPDATED | transactionId={} | merchantId={} | changedFields={}",
                transactionId, merchantId, changedFields);
    }

    public void logTransactionDeleted(Long transactionId, Long merchantId) {
        auditLog.info("TRANSACTION_DELETED | transactionId={} | merchantId={}",
                transactionId, merchantId);
    }

    public void logSearchOperation(Long merchantId, String criteria, int resultCount) {
        auditLog.info("SEARCH_EXECUTED | merchantId={} | criteria={} | resultCount={}",
                merchantId, criteria, resultCount);
    }

    // ============ User Audit Logs ============

    /**
     * 사용자 관련 감사 기록 (일반 목적)
     * @param userId 사용자 ID
     * @param action 수행 작업 (예: created, updated, deleted)
     * @param details 상세 정보
     */
    public void log(Long userId, String action, String details) {
        if (action != null) {
            auditLog.info("USER_EVENT | userId={} | action={} | details={}",
                    userId, action, details);
        } else {
            auditLog.info("USER_EVENT | userId={} | details={}",
                    userId, details);
        }
    }

    /**
     * 사용자 생성 감사 기록
     */
    public void logUserCreated(Long userId, String username, String email) {
        auditLog.info("USER_CREATED | userId={} | username={} | email={}",
                userId, username, email);
    }

    /**
     * 사용자 수정 감사 기록
     */
    public void logUserUpdated(Long userId, String username, String changedFields) {
        auditLog.info("USER_UPDATED | userId={} | username={} | changedFields={}",
                userId, username, changedFields);
    }

    /**
     * 사용자 삭제 감사 기록
     */
    public void logUserDeleted(Long userId, String username) {
        auditLog.info("USER_DELETED | userId={} | username={}",
                userId, username);
    }

    /**
     * 로그인 시도 감사 기록
     */
    public void logLoginAttempt(Long userId, String username, boolean success) {
        String status = success ? "SUCCESS" : "FAILED";
        auditLog.info("LOGIN_ATTEMPT | userId={} | username={} | status={}",
                userId, username, status);
    }

    /**
     * 계정 잠금 감사 기록
     */
    public void logAccountLocked(Long userId, String username, int failedAttempts) {
        auditLog.warn("ACCOUNT_LOCKED | userId={} | username={} | failedAttempts={}",
                userId, username, failedAttempts);
    }

    /**
     * 비밀번호 초기화 감사 기록
     */
    public void logPasswordReset(Long userId, String username) {
        auditLog.info("PASSWORD_RESET | userId={} | username={}",
                userId, username);
    }

    /**
     * 이메일 인증 감사 기록
     */
    public void logEmailVerified(Long userId, String username, String email) {
        auditLog.info("EMAIL_VERIFIED | userId={} | username={} | email={}",
                userId, username, email);
    }
}
