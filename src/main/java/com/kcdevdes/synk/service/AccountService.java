package com.kcdevdes.synk.service;

import com.kcdevdes.synk.entity.AccountEntity;
import com.kcdevdes.synk.entity.UserEntity;
import com.kcdevdes.synk.entity.type.AccountType;
import com.kcdevdes.synk.exception.ErrorCode;
import com.kcdevdes.synk.exception.custom.InvalidInputException;
import com.kcdevdes.synk.exception.custom.ResourceNotFoundException;
import com.kcdevdes.synk.exception.custom.UnauthorizedException;
import com.kcdevdes.synk.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public AccountEntity getAccountById(Long accountId) {
        return accountRepository.findByIdAndDeletedFalse(accountId)
                .orElseThrow(() -> ResourceNotFoundException.account(accountId));
    }

    @Transactional(readOnly = true)
    public AccountEntity getAccountByIdAndUserId(Long accountId, Long userId) {
        return accountRepository.findByIdAndUser_IdAndDeletedFalse(accountId, userId)
                .orElseThrow(() -> UnauthorizedException.accessDenied(accountId));
    }

    @Transactional(readOnly = true)
    public List<AccountEntity> getUserAccounts(Long userId) {
        return accountRepository.findByUser_IdAndDeletedFalse(userId);
    }

    @Transactional(readOnly = true)
    public List<AccountEntity> getUserAccountsByType(Long userId, AccountType accountType) {
        UserEntity user = userService.getUserById(userId);
        return accountRepository.findByUserAndAccountTypeAndDeletedFalse(user, accountType);
    }

    @Transactional
    public AccountEntity createAccount(AccountEntity account, Long userId) {
        UserEntity user = userService.getUserById(userId);
        account.setUser(user);

        if (account.getBalance() == null) {
            account.setBalance(BigDecimal.ZERO);
        }

        if (account.getAccountNumber() != null &&
                accountRepository.existsByAccountNumberAndDeletedFalse(account.getAccountNumber())) {
            throw new InvalidInputException(
                    ErrorCode.INVALID_INPUT_VALUE,
                    "Account number already exists: " + account.getAccountNumber()
            );
        }

        return accountRepository.save(account);
    }

    @Transactional
    public AccountEntity updateAccount(Long accountId, Long userId, AccountEntity accountDetails) {
        AccountEntity account = getAccountByIdAndUserId(accountId, userId);

        if (accountDetails.getAccountName() != null) {
            account.setAccountName(accountDetails.getAccountName());
        }

        if (accountDetails.getAccountType() != null) {
            account.setAccountType(accountDetails.getAccountType());
        }

        if (accountDetails.getCurrency() != null) {
            account.setCurrency(accountDetails.getCurrency());
        }

        if (accountDetails.getBankName() != null) {
            account.setBankName(accountDetails.getBankName());
        }

        if (accountDetails.getDescription() != null) {
            account.setDescription(accountDetails.getDescription());
        }

        if (accountDetails.getActive() != null) {
            account.setActive(accountDetails.getActive());
        }

        return accountRepository.save(account);
    }

    @Transactional
    public void deleteAccount(Long accountId, Long userId) {
        AccountEntity account = getAccountByIdAndUserId(accountId, userId);
        account.setDeleted(true);
        account.setDeletedAt(Instant.now());
        account.setActive(false);
        accountRepository.save(account);
    }

    @Transactional
    public AccountEntity deposit(Long accountId, Long userId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidInputException(
                    ErrorCode.INVALID_INPUT_VALUE,
                    "Deposit amount must be positive"
            );
        }

        AccountEntity account = getAccountByIdAndUserId(accountId, userId);
        account.deposit(amount);
        account.setLastTransactionAt(Instant.now());
        return accountRepository.save(account);
    }

    @Transactional
    public AccountEntity withdraw(Long accountId, Long userId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidInputException(
                    ErrorCode.INVALID_INPUT_VALUE,
                    "Withdrawal amount must be positive"
            );
        }

        AccountEntity account = getAccountByIdAndUserId(accountId, userId);

        if (account.getBalance().compareTo(amount) < 0) {
            throw new InvalidInputException(
                    com.kcdevdes.synk.exception.ErrorCode.INSUFFICIENT_BALANCE,
                    "Insufficient balance. Available: " + account.getFormattedBalance()
            );
        }

        account.withdraw(amount);
        account.setLastTransactionAt(Instant.now());
        return accountRepository.save(account);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalBalance(Long userId) {
        List<AccountEntity> accounts = getUserAccounts(userId);
        return accounts.stream()
                .map(AccountEntity::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
