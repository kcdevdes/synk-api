package com.kcdevdes.synk.exception.custom;

import com.kcdevdes.synk.exception.ErrorCode;

public class UnauthorizedException extends BusinessException {

    public UnauthorizedException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UnauthorizedException(ErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public static UnauthorizedException unauthorized(Long userId) {
        return new UnauthorizedException(
                ErrorCode.UNAUTHORIZED,
                "Authentication required for user: " + userId
        );
    }

    public static UnauthorizedException accessDenied(Long accountId) {
        return new UnauthorizedException(
                ErrorCode.ACCESS_DENIED,
                "Access denied to account: " + accountId
        );
    }
}
