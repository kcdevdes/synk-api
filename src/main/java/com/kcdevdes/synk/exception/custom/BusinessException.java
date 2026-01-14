package com.kcdevdes.synk.exception.custom;

import com.kcdevdes.synk.exception.ErrorCode;
import lombok.Getter;

/**
 * 모든 커스텀 예외의 추상 Base Class
 * - ErrorCode를 받아서 일관된 예외 처리
 */
@Getter
public abstract class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    protected BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    protected BusinessException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
    }

    protected BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
}