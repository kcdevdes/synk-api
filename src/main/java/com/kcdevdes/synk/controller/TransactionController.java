package com.kcdevdes.synk.controller;

import com.kcdevdes.synk.entity.TransactionEntity;
import com.kcdevdes.synk.entity.TransactionType;
import com.kcdevdes.synk.form.TransactionForm;
import com.kcdevdes.synk.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("")
    public ResponseEntity<?>  addNewTransaction(@RequestBody TransactionForm form) {
        try {
            TransactionEntity newTransaction = convert(form);

            TransactionEntity saved = this.transactionService.save(newTransaction);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid transaction type provided."));
        } catch (NullPointerException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing required transaction data."));
        }

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTransactionById(@PathVariable("id") Long id) {
        Optional<TransactionEntity> transactionEntity = this.transactionService.findById(id);
        return transactionEntity
                .map(ResponseEntity::<Object>ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Transaction not found.")));
    }

    @GetMapping("")
    public ResponseEntity<Iterable<TransactionEntity>> getAllTransactions() {
        Iterable<TransactionEntity> all = this.transactionService.findAll();
        return ResponseEntity.ok(all);
    }

    // TODO: Input values could be optional
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTransaction(@PathVariable("id") Long id, @RequestBody TransactionForm form) {
        if (form == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "empty body request"));
        }
        Optional<TransactionEntity> result = this.transactionService.updateById(id, convert(form));

        return result
                .map(ResponseEntity::<Object>ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Transaction not found.")));
    }

    private TransactionEntity convert(TransactionForm form) {
        TransactionEntity newTransaction = new TransactionEntity();
        newTransaction.setType(TransactionType.valueOf(form.getType()));
        newTransaction.setAmount(form.getAmount());
        newTransaction.setMerchant(form.getMerchant());
        newTransaction.setDescription(form.getDescription());

        return newTransaction;
    }

}
