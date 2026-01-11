package com.kcdevdes.synk.service;

import com.kcdevdes.synk.entity.TransactionEntity;
import com.kcdevdes.synk.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;

    // Constructor injection
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    // Service methods
    public TransactionEntity save(TransactionEntity transactionEntity) {
        return transactionRepository.save(transactionEntity);
    }

    public List<TransactionEntity> findAll() {
        return transactionRepository.findAll();
    }

    public Optional<TransactionEntity> findById(Long id) {
        return transactionRepository.findById(id);
    }

    public Optional<TransactionEntity> updateById(Long id, TransactionEntity updated) {
        Optional<TransactionEntity> existingEntity = this.findById(id);
        if (existingEntity.isPresent()) {
            TransactionEntity unwrapped = existingEntity.get();

            // updatable indices: amount, type, merchant, description
            unwrapped.setAmount(updated.getAmount());
            unwrapped.setType(updated.getType());
            unwrapped.setMerchant(updated.getMerchant());
            unwrapped.setDescription(updated.getDescription());

            return Optional.of(transactionRepository.save(unwrapped));
        } else {
            return Optional.empty();
        }
    }
}
