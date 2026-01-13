package com.kcdevdes.synk.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionCreateForm {
    @NotBlank
    public String type;

    @NotNull
    public Double amount;

    @NotBlank
    public String merchant;

    public String description;
}
