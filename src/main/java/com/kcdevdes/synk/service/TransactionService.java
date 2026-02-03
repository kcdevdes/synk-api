package com.kcdevdes.synk.service;

import com.kcdevdes.synk.dto.request.TransactionCreateDTO;
import com.kcdevdes.synk.dto.request.TransactionUpdateDTO;
import com.kcdevdes.synk.entity.AccountEntity;
import com.kcdevdes.synk.entity.TransactionEntity;
import com.kcdevdes.synk.entity.type.TransactionType;
import com.kcdevdes.synk.exception.custom.InvalidInputException;
import com.kcdevdes.synk.exception.custom.ResourceNotFoundException;
import com.kcdevdes.synk.mapper.TransactionMapper;
import com.kcdevdes.synk.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;

    /**
     * Create Transaction from DTO
     * Sets Account and User from accountId in DTO
     * @param dto TransactionCreateDTO
     * @return saved TransactionEntity
     */
    @Transactional
    public TransactionEntity createTransaction(TransactionCreateDTO dto) {
        // Convert DTO to Entity
        TransactionEntity entity = TransactionMapper.toEntity(dto);

        // Fetch Account by accountId from DTO
        AccountEntity account = accountService.getAccountById(dto.getAccountId());

        // Set Account and User
        entity.setAccount(account);
        entity.setUser(account.getUser());

        return transactionRepository.save(entity);
    }

    /**
     * Save Transaction (Direct entity save)
     * @param entity
     * @return
     */
    @Transactional
    public TransactionEntity save(TransactionEntity entity) {
        return transactionRepository.save(entity);
    }

    /**
     * Find All Transactions
     * @return
     */
    public List<TransactionEntity> findAll() {
        return transactionRepository.findByDeletedFalse();
    }

    /**
     * Find Transaction By Id
     * If not found, it will throw ResourceNotFoundException
     * @param id
     * @return
     */
    public TransactionEntity findById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.transaction(id));
    }

    /**
     * Update Transaction By Id
     * @param id
     * @param dto
     * @return
     */
    @Transactional
    public TransactionEntity updateById(Long id, TransactionUpdateDTO dto) {
        TransactionEntity existing = findById(id);
        TransactionMapper.updateEntity(existing, dto);

        return transactionRepository.save(existing);
    }

    /**
     * Delete Transaction By Id
     * It does not perform the actual deletion from the database
     * @param id
     */
    @Transactional
    public void deleteById(Long id) {
        TransactionEntity existing = findById(id);

        // Soft Delete
        existing.setDeleted(true);
        existing.setDeletedAt(Instant.now());

        transactionRepository.save(existing);
    }

    /**
     * Search Transactions By Merchant
     * If merchant is empty, it will throw InvalidInputException
     * @param merchant
     * @return
     */
    public List<TransactionEntity> searchTransactionsByMerchant(String merchant) {
        if (merchant == null || merchant.isBlank()) {
            throw InvalidInputException.currency("Merchant query cannot be empty");
        }

        return transactionRepository.findByMerchantContainingIgnoreCase(merchant);
    }

    /**
     * Filter Transactions By Type
     * If typeString is empty, it will throw InvalidInputException
     * @param typeString
     * @return
     */
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

        return transactionRepository.findByType(type);
    }

    /**
     * Find Transactions By User Id
     * @param userId
     * @return
     */
    public List<TransactionEntity> findByUserId(Long userId) {
        return transactionRepository.findByUserIdAndDeletedFalse(userId);
    }

    /**
     * Find Transactions By Account Id
     * @param accountId
     * @return
     */
    public List<TransactionEntity> findByAccountId(Long accountId) {
        return transactionRepository.findByAccountId(accountId);
    }

    /**
     * Find Transactions By User Id And Type
     * @param userId
     * @param type
     * @return
     */
    public List<TransactionEntity> findByUserIdAndType(Long userId, TransactionType type) {
        return transactionRepository.findByUserIdAndType(userId, type);
    }
}
