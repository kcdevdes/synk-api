package com.kcdevdes.synk.util;

import com.kcdevdes.synk.exception.ErrorCode;
import com.kcdevdes.synk.exception.custom.InvalidInputException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

public final class InputSanitizer {

    private static final Logger log = LoggerFactory.getLogger(InputSanitizer.class);
    private static final Pattern SUSPICIOUS_PATTERN =
            Pattern.compile("(?i)(<\\s*script|</\\s*script|javascript:|on\\w+\\s*=)");

    private InputSanitizer() {
    }

    public static String sanitizePlainText(String value, String fieldName) {
        if (value == null) {
            return null;
        }

        String sanitized = value.trim().replaceAll("\\p{Cntrl}", "");
        if (SUSPICIOUS_PATTERN.matcher(sanitized).find()) {
            log.warn("event=suspicious_input field={} valueLength={}", fieldName, sanitized.length());
            throw new InvalidInputException(
                    ErrorCode.INVALID_INPUT_VALUE,
                    "Suspicious input detected in field: " + fieldName
            );
        }

        return sanitized;
    }
}
