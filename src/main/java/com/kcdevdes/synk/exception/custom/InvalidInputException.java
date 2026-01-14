package com.kcdevdes.synk.exception.custom;

import com.kcdevdes.synk.exception.ErrorCode;

/**
 * 잘못된 입력값
 * - Validation 실패
 * - 비즈니스 로직 위반
 */
public class InvalidInputException extends BusinessException {

    public InvalidInputException(ErrorCode errorCode) {
        super(errorCode);
    }

    public InvalidInputException(ErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    // 편의 메서드
    public static InvalidInputException transactionType(String type) {
        return new InvalidInputException(
                ErrorCode.INVALID_TRANSACTION_TYPE,
                "Invalid transaction type: " + type
        );
    }

    public static InvalidInputException currency(String currency) {
        return new InvalidInputException(
                ErrorCode.INVALID_CURRENCY,
                "Invalid currency code: " + currency
        );
    }
}