package com.kcdevdes.synk.dto.request;

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
    private BigDecimal amount;
    private String merchant;
    private String category;
    private String description;
    private String tags;
}
