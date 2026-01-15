package com.kcdevdes.synk.controller;

import com.kcdevdes.synk.dto.request.TransactionCreateDTO;
import com.kcdevdes.synk.dto.request.TransactionUpdateDTO;
import com.kcdevdes.synk.dto.response.TransactionDTO;
import com.kcdevdes.synk.entity.TransactionEntity;
import com.kcdevdes.synk.entity.type.TransactionType;
import com.kcdevdes.synk.exception.custom.InvalidInputException;
import com.kcdevdes.synk.mapper.TransactionMapper;
import com.kcdevdes.synk.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionDTO> createTransaction(
            @Valid @RequestBody TransactionCreateDTO dto
    ) {
        TransactionEntity entity = TransactionMapper.toEntity(dto);
        TransactionEntity saved = transactionService.save(entity);
        TransactionDTO responseDTO = TransactionMapper.toDTO(saved);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> getTransaction(@PathVariable Long id) {
        TransactionEntity entity = transactionService.findById(id);
        TransactionDTO dto = TransactionMapper.toDTO(entity);

        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<TransactionDTO>> getAllTransactions() {
        List<TransactionEntity> entities = transactionService.findAll();
        List<TransactionDTO> dtos = TransactionMapper.toDTOList(entities);

        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionDTO> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody TransactionUpdateDTO dto
    ) {
        TransactionEntity updated = transactionService.updateById(id, dto);
        TransactionDTO responseDTO = TransactionMapper.toDTO(updated);

        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<TransactionDTO>> searchTransactions(
            @RequestParam String query
    ) {
        List<TransactionEntity> results = transactionService.searchTransactionsByMerchant(query);
        List<TransactionDTO> dtos = TransactionMapper.toDTOList(results);

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<TransactionDTO>> filterTransactions(
            @RequestParam String type
    ) {
        List<TransactionEntity> results = transactionService.filterTransactionByType(type);
        List<TransactionDTO> dtos = TransactionMapper.toDTOList(results);

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByUser(
            @PathVariable Long userId
    ) {
        List<TransactionEntity> entities = transactionService.findByUserId(userId);
        List<TransactionDTO> dtos = TransactionMapper.toDTOList(entities);

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByAccount(
            @PathVariable Long accountId
    ) {
        List<TransactionEntity> entities = transactionService.findByAccountId(accountId);
        List<TransactionDTO> dtos = TransactionMapper.toDTOList(entities);

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/user/{userId}/type/{type}")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByUserAndType(
            @PathVariable Long userId,
            @PathVariable String type
    ) {
        TransactionType transactionType;
        try {
            transactionType = TransactionType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw InvalidInputException.transactionType(type);
        }

        List<TransactionEntity> entities = transactionService.findByUserIdAndType(userId, transactionType);

        List<TransactionDTO> dtos = TransactionMapper.toDTOList(entities);

        return ResponseEntity.ok(dtos);
    }
}
