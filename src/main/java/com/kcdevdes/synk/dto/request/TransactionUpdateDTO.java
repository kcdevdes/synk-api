package com.kcdevdes.synk.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionUpdateDTO {
    public String type;
    public Double amount;
    public String merchant;
    public String description;
}
