package com.kcdevdes.synk.exception.custom;

import com.kcdevdes.synk.exception.ErrorCode;

import java.math.BigDecimal;

/**
 * 잔액 부족 (비즈니스 로직 예외)
 */
public class InsufficientBalanceException extends BusinessException {

    public InsufficientBalanceException() {
        super(ErrorCode.INSUFFICIENT_BALANCE);
    }

    public InsufficientBalanceException(BigDecimal balance, BigDecimal required) {
        super(
                ErrorCode.INSUFFICIENT_BALANCE,
                String.format("Insufficient balance. Available: %s, Required: %s", balance, required)
        );
    }
}