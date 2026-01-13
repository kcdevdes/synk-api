package com.kcdevdes.synk.controller;

import com.kcdevdes.synk.entity.TransactionEntity;
import com.kcdevdes.synk.entity.TransactionType;
import com.kcdevdes.synk.form.TransactionCreateForm;
import com.kcdevdes.synk.form.TransactionUpdateForm;
import com.kcdevdes.synk.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.List;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("")
    public ResponseEntity<?>  addNewTransaction(@Valid @RequestBody TransactionCreateForm form) {
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

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTransaction(@PathVariable("id") Long id, @RequestBody TransactionUpdateForm form) {
        if (form == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "empty body request"));
        }
        Optional<TransactionEntity> result = this.transactionService.updateById(id, convert(form));

        return result
                .map(ResponseEntity::<Object>ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Transaction not found.")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransaction(@PathVariable("id") Long id) {
        Optional<TransactionEntity> result= this.transactionService.deleteById(id);

        return result
                .map(ResponseEntity::<Object>ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Transaction not found.")));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchTransactions(@RequestParam("query") String query) {
        if (query == null || query.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        List<TransactionEntity> results = this.transactionService.searchTransactionsByMerchant(query);

        return ResponseEntity.ok().body(results);
    }

    private TransactionEntity convert(TransactionCreateForm form) {
        TransactionEntity entity = new TransactionEntity();
        entity.setType(TransactionType.valueOf(form.getType()));
        entity.setAmount(form.getAmount());
        entity.setMerchant(form.getMerchant());
        entity.setDescription(form.getDescription());
        return entity;
    }

    private TransactionEntity convert(TransactionUpdateForm form) {
        TransactionEntity entity = new TransactionEntity();

        if (form.getType() != null) {
            entity.setType(TransactionType.valueOf(form.getType()));
        }
        if (form.getAmount() != null) {
            entity.setAmount(form.getAmount());
        }
        if (form.getMerchant() != null) {
            entity.setMerchant(form.getMerchant());
        }
        if (form.getDescription() != null) {
            entity.setDescription(form.getDescription());
        }

        return entity;
    }

}
