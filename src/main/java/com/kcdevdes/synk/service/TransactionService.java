package com.kcdevdes.synk.service;

import com.kcdevdes.synk.dto.request.TransactionUpdateDTO;
import com.kcdevdes.synk.entity.TransactionEntity;
import com.kcdevdes.synk.entity.type.TransactionType;
import com.kcdevdes.synk.exception.custom.InvalidInputException;
import com.kcdevdes.synk.exception.custom.ResourceNotFoundException;
import com.kcdevdes.synk.mapper.TransactionMapper;
import com.kcdevdes.synk.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionService {

    private final TransactionRepository transactionRepository;

    @Transactional
    public TransactionEntity save(TransactionEntity entity) {
        log.info("Creating transaction: merchant={}, amount={}",
                entity.getMerchant(), entity.getAmount());

        return transactionRepository.save(entity);
    }

    public List<TransactionEntity> findAll() {
        return transactionRepository.findByDeletedFalse();
    }

    public TransactionEntity findById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.transaction(id));
    }

    @Transactional
    public TransactionEntity updateById(Long id, TransactionUpdateDTO dto) {
        TransactionEntity existing = findById(id);
        TransactionMapper.updateEntity(existing, dto);
        log.info("Updating transaction: id={}", id);

        return transactionRepository.save(existing);
    }

    @Transactional
    public void deleteById(Long id) {
        TransactionEntity existing = findById(id);

        // Soft Delete
        existing.setDeleted(true);
        existing.setDeletedAt(Instant.now());

        transactionRepository.save(existing);

        log.info("Soft deleted transaction: id={}", id);
    }

    public List<TransactionEntity> searchTransactionsByMerchant(String merchant) {
        if (merchant == null || merchant.isBlank()) {
            throw InvalidInputException.currency("Merchant query cannot be empty");
        }

        log.debug("Searching transactions by merchant: {}", merchant);
        return transactionRepository.findByMerchantContainingIgnoreCase(merchant);
    }

    public List<TransactionEntity> filterTransactionByType(String typeString) {
        if (typeString == null || typeString.isBlank()) {
            throw InvalidInputException.transactionType("Transaction type cannot be empty");
        }

        TransactionType type;
        try {
            type = TransactionType.valueOf(typeString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw InvalidInputException.transactionType(typeString);
        }

        log.debug("Filtering transactions by type: {}", type);
        return transactionRepository.findByType(type);
    }

    public List<TransactionEntity> findByUserId(Long userId) {
        return transactionRepository.findByUserIdAndDeletedFalse(userId);
    }

    public List<TransactionEntity> findByAccountId(Long accountId) {
        return transactionRepository.findByAccountId(accountId);
    }

    public List<TransactionEntity> findByUserIdAndType(Long userId, TransactionType type) {
        return transactionRepository.findByUserIdAndType(userId, type);
    }
}
