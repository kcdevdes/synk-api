package com.kcdevdes.synk.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * id                    // Long
 * accountName           // String "신한은행 계좌"
 * accountType           // String "BANK_ACCOUNT"
 * currency              // String "USD"
 * balance               // BigDecimal
 * accountNumber         // String (마스킹: "****1234")
 * bankName              // String
 * description           // String
 * active                // Boolean
 * createdAt             // Instant
 * updatedAt             // Instant
 * lastTransactionAt     // Instant
 */

@Getter
@Setter
@NoArgsConstructor
public class AccountDTO {
    private Long id;
    private String accountName;
    private String accountType;
    private String currency;
    private BigDecimal balance;
    private String accountNumber;
    private String bankName;
    private String description;
    private Boolean active;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant lastTransactionAt;
}
