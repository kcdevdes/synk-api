package com.kcdevdes.synk.repository;

import com.kcdevdes.synk.entity.AccountEntity;
import com.kcdevdes.synk.entity.UserEntity;
import com.kcdevdes.synk.entity.type.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    // User account queries
    List<AccountEntity> findByUserAndDeletedFalse(UserEntity user);
    List<AccountEntity> findByUser_IdAndDeletedFalse(Long userId);

    // Account type queries
    List<AccountEntity> findByUserAndAccountTypeAndDeletedFalse(UserEntity user, AccountType accountType);

    // Active account queries
    Optional<AccountEntity> findByIdAndDeletedFalse(Long id);
    Optional<AccountEntity> findByIdAndUser_IdAndDeletedFalse(Long accountId, Long userId);

    // Account number queries
    Optional<AccountEntity> findByAccountNumberAndDeletedFalse(String accountNumber);
    boolean existsByAccountNumberAndDeletedFalse(String accountNumber);
}
