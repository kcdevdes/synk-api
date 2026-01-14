package com.kcdevdes.synk.dto.request;

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
    private String accountName;
    private BigDecimal balance;
    private String description;
    private Boolean active;
}
