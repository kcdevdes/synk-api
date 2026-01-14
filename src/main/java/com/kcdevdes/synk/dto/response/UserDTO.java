package com.kcdevdes.synk.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * id                    // Long
 * email                 // String
 * username              // String
 * firstName             // String
 * lastName              // String
 * mobile                // String
 * defaultCurrency       // String
 * locale                // String
 * timezone              // String
 * active                // Boolean
 * emailVerified         // Boolean
 * createdAt             // Instant
 * updatedAt             // Instant
 * lastLoginAt           // Instant
 */

@Getter
@Setter
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String email;
    private String username;
    private String firstName;
    private String lastName;
    private String mobile;
    private String defaultCurrency;
    private String locale;
    private String timezone;
    private Boolean active;
    private Boolean emailVerified;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant lastLoginAt;
}


