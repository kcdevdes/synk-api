package com.kcdevdes.synk.controller;

import com.kcdevdes.synk.entity.TransactionEntity;
import com.kcdevdes.synk.entity.type.TransactionType;
import com.kcdevdes.synk.dto.request.TransactionCreateDTO;
import com.kcdevdes.synk.dto.request.TransactionUpdateDTO;
import com.kcdevdes.synk.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("")
    public ResponseEntity<?>  addNewTransaction(@Valid @RequestBody TransactionCreateDTO dto) {
        try {
            TransactionEntity newTransaction = convert(dto);

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
    public ResponseEntity<?> updateTransaction(@PathVariable("id") Long id, @RequestBody TransactionUpdateDTO dto) {
        if (dto == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "empty body request"));
        }
        Optional<TransactionEntity> result = this.transactionService.updateById(id, convert(dto));

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

    @GetMapping("/filter")
    public ResponseEntity<?> filterTransactions(@RequestParam("query") String query) {
        if (query == null || query.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        List<TransactionEntity> results = this.transactionService.filterTransactionByType(query);

        return ResponseEntity.ok().body(results);
    }

    private TransactionEntity convert(TransactionCreateDTO dto) {
        TransactionEntity entity = new TransactionEntity();
        entity.setType(TransactionType.valueOf(dto.getType()));
        entity.setMerchant(dto.getMerchant());
        entity.setDescription(dto.getDescription());
        return entity;
    }

    private TransactionEntity convert(TransactionUpdateDTO dto) {
        TransactionEntity entity = new TransactionEntity();

        if (dto.getType() != null) {
            entity.setType(TransactionType.valueOf(dto.getType()));
        }
        if (dto.getMerchant() != null) {
            entity.setMerchant(dto.getMerchant());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }

        return entity;
    }

}
