package com.kcdevdes.synk.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * success               // Boolean true
 * data                  // T (제네릭)
 * timestamp             // Instant
 * message               // String (선택)
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SuccessResponse<T> {
    private Boolean success;
    private T data;
    private Instant timestamp = Instant.now();
    private String message;
}
