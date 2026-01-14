package com.kcdevdes.synk.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * id                    // Long
 * type                  // String "INCOME"
 * amount                // BigDecimal
 * merchant              // String "스타벅스"
 * currency              // String "USD"
 * category              // String "식비"
 * description           // String
 * tags                  // String "커피,출장"
 * paymentMethod         // String "CREDIT_CARD"
 * accountId             // Long (FK만)
 * accountName           // String (조인 데이터) "BC카드"
 * occurredAt            // Instant
 * updatedAt             // Instant
 * deleted               // Boolean
 *
 * // 환율 정보 (선택)
 * originalAmount        // BigDecimal
 * originalCurrency      // String
 * exchangeRate          // BigDecimal
 */

@Getter
@Setter
@NoArgsConstructor
public class TransactionDTO {
    private Long id;
    private String type;
    private BigDecimal amount;
    private String merchant;
    private String currency;
    private String description;
    private String category;
    private String tags;
    private String paymentMethod;
    private Long accountId;
    private String accountName;
    private Instant occurredAt;
    private Instant updatedAt;
    private Boolean deleted;

    /** Current Exchange Rate **/
    private BigDecimal originalAmount;
    private String originalCurrency;
    private BigDecimal exchangeRate;

}