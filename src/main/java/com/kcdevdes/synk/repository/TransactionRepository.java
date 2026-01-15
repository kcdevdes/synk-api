package com.kcdevdes.synk.repository;

import com.kcdevdes.synk.entity.TransactionEntity;
import com.kcdevdes.synk.entity.type.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

    List<TransactionEntity> findByMerchantContainingIgnoreCase(String merchant);
    List<TransactionEntity> findByType(TransactionType type);
    List<TransactionEntity> findByUserId(Long userId);
    List<TransactionEntity> findByAccountId(Long accountId);
    List<TransactionEntity> findByDeletedFalse();
    List<TransactionEntity> findByUserIdAndDeletedFalse(Long userId);

    // === 복합 조건 검색 ===

    List<TransactionEntity> findByUserIdAndType(
            Long userId,
            TransactionType type
    );

    List<TransactionEntity> findByAccountIdAndOccurredAtBetween(
            Long accountId,
            Instant startDate,
            Instant endDate
    );

    List<TransactionEntity> findByUserIdAndOccurredAtBetween(
            Long userId,
            Instant startDate,
            Instant endDate
    );
}
