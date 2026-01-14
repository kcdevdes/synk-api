package com.kcdevdes.synk.exception.custom;

import com.kcdevdes.synk.exception.ErrorCode;

/**
 * 리소스 중복
 * - 이미 존재하는 이메일, 유저명 등
 */
public class DuplicateResourceException extends BusinessException {

    public DuplicateResourceException(ErrorCode errorCode) {
        super(errorCode);
    }

    public DuplicateResourceException(ErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    // 편의 메서드
    public static DuplicateResourceException email(String email) {
        return new DuplicateResourceException(
                ErrorCode.DUPLICATE_EMAIL,
                "Email already exists: " + email
        );
    }

    public static DuplicateResourceException username(String username) {
        return new DuplicateResourceException(
                ErrorCode.DUPLICATE_USERNAME,
                "Username already exists: " + username
        );
    }
}