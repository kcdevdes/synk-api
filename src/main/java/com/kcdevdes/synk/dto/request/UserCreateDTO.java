package com.kcdevdes.synk.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * email                 // @Email @NotBlank
 * username              // @NotBlank @Size(min=3, max=50)
 * password              // @NotBlank @Size(min=8) @Pattern(보안 규칙)
 * firstName             // @NotBlank
 * lastName              // @NotBlank
 * mobile                // @Pattern(전화번호 형식) (선택)
 * defaultCurrency       // @NotNull (기본값: "USD")
 * locale                // (선택)
 * timezone              // (선택)
 */

@Getter
@Setter
@NoArgsConstructor
public class UserCreateDTO {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 4, max = 64)
    private String username;

    @NotBlank
    @Size(min = 8)
    private String password;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$")
    private String mobile;

    @NotNull
    private String defaultCurrency = "USD";

    private String locale;
    private String timezone;
}
