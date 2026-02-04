package com.kcdevdes.synk.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * accountName           // (선택)
 * balance               // (선택) BigDecimal
 * description           // (선택)
 * active                // (선택) Boolean
 */

@Getter
@Setter
@NoArgsConstructor
public class AccountUpdateDTO {
    @Size(max = 128)
    @Pattern(regexp = "^[A-Za-z0-9 .\\-']+$")
    private String accountName;

    @Positive
    private BigDecimal balance;

    @Size(max = 512)
    @Pattern(regexp = "^[^<>]*$")
    private String description;
    private Boolean active;
}
