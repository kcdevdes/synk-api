package com.kcdevdes.synk.exception.custom;

import com.kcdevdes.synk.exception.ErrorCode;

/**
 * 리소스를 찾을 수 없을 때
 * - User, Account, Transaction 등
 */
public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ResourceNotFoundException(ErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    // 편의 생성자
    public static ResourceNotFoundException user(Long userId) {
        return new ResourceNotFoundException(
                ErrorCode.USER_NOT_FOUND,
                "User not found with id: " + userId
        );
    }

    public static ResourceNotFoundException account(Long accountId) {
        return new ResourceNotFoundException(
                ErrorCode.ACCOUNT_NOT_FOUND,
                "Account not found with id: " + accountId
        );
    }

    public static ResourceNotFoundException transaction(Long transactionId) {
        return new ResourceNotFoundException(
                ErrorCode.TRANSACTION_NOT_FOUND,
                "Transaction not found with id: " + transactionId
        );
    }
}