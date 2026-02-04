package com.kcdevdes.synk.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * firstName             // (선택)
 * lastName              // (선택)
 * mobile                // (선택)
 * defaultCurrency       // (선택)
 * locale                // (선택)
 * timezone              // (선택)
 */

@Getter
@Setter
@NoArgsConstructor
public class UserUpdateDTO {
    @Pattern(regexp = "^[A-Za-z .'-]+$")
    private String firstName;

    @Pattern(regexp = "^[A-Za-z .'-]+$")
    private String lastName;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$")
    private String mobile;

    @Size(min = 3, max = 3)
    @Pattern(regexp = "^[A-Z]{3}$")
    private String defaultCurrency;
    private String locale;
    private String timezone;
}
