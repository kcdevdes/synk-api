package com.kcdevdes.synk.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Map;

/**
 * errorCode             // String "VALIDATION_ERROR"
 * message               // String "Invalid input"
 * timestamp             // Instant
 * path                  // String "/api/transactions"
 * fieldErrors           // Map<String, String> {"amount": "must be positive"}
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String errorCode;
    private String message;
    private Instant timestamp = Instant.now();
    private String path;
    private Map<String, String> fieldErrors;
}
