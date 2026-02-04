package com.kcdevdes.synk.dto.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 *
 * amount                // (선택)
 * merchant              // (선택)
 * category              // (선택)
 * description           // (선택)
 * tags                  // (선택)
 */

@Getter
@Setter
@NoArgsConstructor
public class TransactionUpdateDTO {
    @Positive
    @Digits(integer = 10, fraction = 2)
    private BigDecimal amount;

    @Size(max = 128)
    @Pattern(regexp = "^[A-Za-z0-9 .\\-']+$")
    private String merchant;

    @Size(max = 64)
    @Pattern(regexp = "^[^<>]*$")
    private String category;

    @Size(max = 512)
    @Pattern(regexp = "^[^<>]*$")
    private String description;

    @Size(max = 256)
    @Pattern(regexp = "^[^<>]*$")
    private String tags;
}
