package com.kcdevdes.synk.service;

import com.kcdevdes.synk.entity.TransactionEntity;
import com.kcdevdes.synk.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
