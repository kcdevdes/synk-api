package com.kcdevdes.synk.repository;

import com.kcdevdes.synk.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    TransactionEntity findById(long id);
    TransactionEntity findByMerchant(String merchant);
}
