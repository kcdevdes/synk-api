package com.kcdevdes.synk.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 type                  // @NotNull @Pattern("INCOME|EXPENSE|TRANSFER")
 amount                // @NotNull @Positive @Digits(integer=10, fraction=2)
 merchant              // @NotBlank @Size(max=128)
 currency              // @NotNull @Size(3) "USD"
 category              // @Size(max=64) (선택)
 description           // @Size(max=512) (선택)
 tags                  // @Size(max=256) (선택)
 paymentMethod         // @Pattern("CREDIT_CARD|CASH|...") (선택)
 accountId             // @NotNull (필수 - 어느 계좌?)

 // 환율 (선택)
 originalAmount        // (선택)
 originalCurrency      // (선택)
 exchangeRate          // (선택)
 */

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
    private String merchant;

    @NotNull
    @Size(min = 3, max = 3)
    private String currency;

    @Size(max = 64)
    private String category;

    @Size(max = 512)
    private String description;

    @Size(max = 256)
    private String tags;

    @Pattern(regexp = "CREDIT_CARD|DEBIT_CARD|PAYPAL|BANK_TRANSFER|CASH|OTHER")
    private String paymentMethod;

    @NotNull
    private Long accountId;

    /** Current Exchange Rate **/
    private BigDecimal originalAmount;
    private String originalCurrency;
    private BigDecimal exchangeRate;
}
