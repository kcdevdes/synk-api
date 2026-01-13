package com.kcdevdes.synk.service;

import com.kcdevdes.synk.entity.TransactionEntity;
import com.kcdevdes.synk.entity.TransactionType;
import com.kcdevdes.synk.repository.TransactionRepository;
import jakarta.servlet.Filter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

            boolean changed = false;

            // updatable indices: amount, type, merchant, description
            // Only changed values will be applied
            if (updated.getAmount() != null && !java.util.Objects.equals(updated.getAmount(), unwrapped.getAmount())) {
                unwrapped.setAmount(updated.getAmount());
                changed = true;
            }
            if (updated.getType() != null && !java.util.Objects.equals(updated.getType(), unwrapped.getType())) {
                unwrapped.setType(updated.getType());
                changed = true;
            }
            if (updated.getMerchant() != null && !java.util.Objects.equals(updated.getMerchant(), unwrapped.getMerchant())) {
                unwrapped.setMerchant(updated.getMerchant());
                changed = true;
            }
            if (updated.getDescription() != null && !java.util.Objects.equals(updated.getDescription(), unwrapped.getDescription())) {
                unwrapped.setDescription(updated.getDescription());
                changed = true;
            }

            // If nothing changed, avoid an unnecessary write. Return the existing entity as-is.
            if (!changed) {
                return Optional.of(unwrapped);
            }

            return Optional.of(transactionRepository.save(unwrapped));
        } else {
            return Optional.empty();
        }
    }

    public Optional<TransactionEntity> deleteById(Long id) {
        Optional<TransactionEntity> existingEntity = this.findById(id);

        if (existingEntity.isEmpty()) {
            return Optional.empty();
        }

        this.transactionRepository.deleteById(id);

        return existingEntity;
    }

    public List<TransactionEntity> searchTransactionsByMerchant(String merchantName) {
        ArrayList<TransactionEntity> found = new ArrayList<>();
        List<TransactionEntity> original = this.findAll();
        for (TransactionEntity each : original) {
            if (each.getMerchant().toLowerCase().contains(merchantName.toLowerCase())) {
                found.add(each);
            }
        }

        return found;
    }

    public List<TransactionEntity> filterTransactionByType(String type) {
        ArrayList<TransactionEntity> filtered = new ArrayList<>();

        try {
            for (TransactionEntity each: this.findAll()) {
                if (each.getType() == TransactionType.valueOf(type.toUpperCase())) {
                    filtered.add(each);
                }
            }

            return filtered;
        } catch (Exception e) {
            return null;
        }
    }
}
