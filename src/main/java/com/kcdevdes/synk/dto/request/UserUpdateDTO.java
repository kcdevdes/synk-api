package com.kcdevdes.synk.dto.request;

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
    private String firstName;
    private String lastName;
    private String mobile;
    private String defaultCurrency;
    private String locale;
    private String timezone;
}
