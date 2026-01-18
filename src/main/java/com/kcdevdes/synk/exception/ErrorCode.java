package com.kcdevdes.synk.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // === 공통 에러 (1000번대) ===
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "E1001", "Invalid input value"),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "E1002", "Invalid type value"),
    MISSING_INPUT_VALUE(HttpStatus.BAD_REQUEST, "E1003", "Missing required field"),

    // === 인증/인가 에러 (2000번대) ===
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "E2001", "Authentication required"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "E2002", "Access denied"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "E2003", "Invalid token"),

    // === 리소스 에러 (3000번대) ===
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "E3001", "Resource not found"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "E3002", "User not found"),
    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "E3003", "Account not found"),
    TRANSACTION_NOT_FOUND(HttpStatus.NOT_FOUND, "E3004", "Transaction not found"),

    // === 중복 에러 (4000번대) ===
    DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "E4001", "Resource already exists"),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "E4002", "Email already exists"),
    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "E4003", "Username already exists"),

    // === 비즈니스 로직 에러 (5000번대) ===
    INSUFFICIENT_BALANCE(HttpStatus.BAD_REQUEST, "E5001", "Insufficient account balance"),
    INVALID_TRANSACTION_TYPE(HttpStatus.BAD_REQUEST, "E5002", "Invalid transaction type"),
    ACCOUNT_INACTIVE(HttpStatus.BAD_REQUEST, "E5003", "Account is inactive"),
    INVALID_CURRENCY(HttpStatus.BAD_REQUEST, "E5004", "Invalid currency code"),

    // === 서버 에러 (9000번대) ===
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E9001", "Internal server error"),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E9002", "Database error");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}