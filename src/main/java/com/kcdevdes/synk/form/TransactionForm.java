package com.kcdevdes.synk.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionForm {
    public String type;
    public Double amount;
    public String merchant;
    public String description;
}
