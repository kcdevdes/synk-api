package com.kcdevdes.synk.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class TransactionCreateDTO {
    @NotNull
    @Pattern(regexp = "INCOME|EXPENSE|TRANSFER")
    private String type;

    @NotNull
    @Positive
    @Digits(integer = 10, fraction = 2)
    private BigDecimal amount;

    @NotBlank
    @Size(max = 128)
    @Pattern(regexp = "^[A-Za-z0-9 .\\-']+$")
    private String merchant;

    @NotNull
    @Size(min = 3, max = 3)
    @Pattern(regexp = "^[A-Z]{3}$")
    private String currency;

    @Size(max = 64)
    @Pattern(regexp = "^[^<>]*$")
    private String category;

    @Size(max = 512)
    @Pattern(regexp = "^[^<>]*$")
    private String description;

    @Size(max = 256)
    @Pattern(regexp = "^[^<>]*$")
    private String tags;

    @Pattern(regexp = "CREDIT_CARD|DEBIT_CARD|PAYPAL|BANK_TRANSFER|CASH|OTHER")
    private String paymentMethod;

    @NotNull
    private Long accountId;

    /** Current Exchange Rate **/
    private BigDecimal originalAmount;

    @Pattern(regexp = "^[A-Z]{3}$")
    private String originalCurrency;
    private BigDecimal exchangeRate;
}
