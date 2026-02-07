package com.kcdevdes.synk.util;

import com.kcdevdes.synk.exception.custom.InvalidInputException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("InputSanitizer Unit Test")
class InputSanitizerTest {

    @Test
    @DisplayName("sanitizePlainText trims and removes control characters")
    void sanitizePlainText_TrimsAndRemovesControlChars() {
        String value = "  Hello\u0007 World  ";
        String sanitized = InputSanitizer.sanitizePlainText(value, "testField");
        assertEquals("Hello World", sanitized);
    }

    @Test
    @DisplayName("sanitizePlainText rejects suspicious input")
    void sanitizePlainText_RejectsSuspiciousInput() {
        assertThrows(InvalidInputException.class,
                () -> InputSanitizer.sanitizePlainText("<script>alert(1)</script>", "merchant"));
    }
}
