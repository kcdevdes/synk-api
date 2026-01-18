package com.kcdevdes.synk.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * accountName           // @NotBlank "신한 체크카드"
 * accountType           // @NotNull @Pattern(BANK_ACCOUNT, CREDIT_CARD, CASH, WALLET)
 * currency              // @NotNull @Size(3) "USD"
 * balance               // @NotNull @Positive
 * accountNumber         // (선택) @Size(max=32)
 * bankName              // (선택) @Size(max=32)
 * description           // (선택) @Size(max=512)
 */

@Getter
@Setter
@NoArgsConstructor
public class AccountCreateDTO {
    @NotBlank
    private String accountName;

    @NotBlank @Pattern(regexp = "BANK_ACCOUNT|CREDIT_CARD|CASH|WALLET")
    private String accountType;

    @NotNull
    @Size(min = 3, max = 3)
    private String currency;

    @NotNull
    @Positive
    private BigDecimal balance;

    @Size(max = 32)
    private String accountNumber;

    @Size(max = 32)
    private String bankName;

    @Size(max = 512)
    private String description;
}
