package com.kcdevdes.synk.controller;

import com.kcdevdes.synk.entity.TransactionEntity;
import com.kcdevdes.synk.entity.TransactionType;
import com.kcdevdes.synk.form.TransactionForm;
import com.kcdevdes.synk.service.TransactionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping({"", "/"})
    public String addNewTransaction(@RequestBody TransactionForm form) {
        try {
            TransactionEntity newTransaction = new TransactionEntity();
            newTransaction.setType(TransactionType.valueOf(form.getType()));
            newTransaction.setAmount(form.getAmount());
            newTransaction.setMerchant(form.getMerchant());
            newTransaction.setDescription(form.getDescription());

            return this.transactionService.save(newTransaction).toString();
        } catch (IllegalArgumentException e) {
            return "Error: Invalid transaction type provided.";
        } catch (NullPointerException e) {
            return "Error: Missing required transaction data.";
        }

    }
}
