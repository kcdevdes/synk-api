package com.kcdevdes.synk.controller;

import com.kcdevdes.synk.dto.request.AccountCreateDTO;
import com.kcdevdes.synk.dto.request.AccountUpdateDTO;
import com.kcdevdes.synk.dto.response.AccountDTO;
import com.kcdevdes.synk.entity.AccountEntity;
import com.kcdevdes.synk.entity.type.AccountType;
import com.kcdevdes.synk.mapper.AccountMapper;
import com.kcdevdes.synk.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountDTO> getAccountById(@PathVariable Long accountId) {
        AccountEntity account = accountService.getAccountById(accountId);
        return ResponseEntity.ok(AccountMapper.toDTO(account));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AccountDTO>> getUserAccounts(@PathVariable Long userId) {
        List<AccountEntity> accounts = accountService.getUserAccounts(userId);
        return ResponseEntity.ok(AccountMapper.toDTOList(accounts));
    }

    @GetMapping("/user/{userId}/type/{accountType}")
    public ResponseEntity<List<AccountDTO>> getUserAccountsByType(
            @PathVariable Long userId,
            @PathVariable AccountType accountType) {
        List<AccountEntity> accounts = accountService.getUserAccountsByType(userId, accountType);
        return ResponseEntity.ok(AccountMapper.toDTOList(accounts));
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<AccountDTO> createAccount(
            @PathVariable Long userId,
            @Valid @RequestBody AccountCreateDTO createDTO) {
        AccountEntity entity = AccountMapper.toEntity(createDTO);
        AccountEntity created = accountService.createAccount(entity, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(AccountMapper.toDTO(created));
    }

    @PutMapping("/{accountId}/user/{userId}")
    public ResponseEntity<AccountDTO> updateAccount(
            @PathVariable Long accountId,
            @PathVariable Long userId,
            @Valid @RequestBody AccountUpdateDTO updateDTO) {
        AccountEntity entity = new AccountEntity();
        AccountMapper.updateEntity(entity, updateDTO);
        AccountEntity updated = accountService.updateAccount(accountId, userId, entity);
        return ResponseEntity.ok(AccountMapper.toDTO(updated));
    }

    @DeleteMapping("/{accountId}/user/{userId}")
    public ResponseEntity<Void> deleteAccount(
            @PathVariable Long accountId,
            @PathVariable Long userId) {
        accountService.deleteAccount(accountId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{accountId}/user/{userId}/deposit")
    public ResponseEntity<AccountDTO> deposit(
            @PathVariable Long accountId,
            @PathVariable Long userId,
            @RequestBody Map<String, BigDecimal> request) {
        BigDecimal amount = request.get("amount");
        AccountEntity account = accountService.deposit(accountId, userId, amount);
        return ResponseEntity.ok(AccountMapper.toDTO(account));
    }

    @PostMapping("/{accountId}/user/{userId}/withdraw")
    public ResponseEntity<AccountDTO> withdraw(
            @PathVariable Long accountId,
            @PathVariable Long userId,
            @RequestBody Map<String, BigDecimal> request) {
        BigDecimal amount = request.get("amount");
        AccountEntity account = accountService.withdraw(accountId, userId, amount);
        return ResponseEntity.ok(AccountMapper.toDTO(account));
    }

    @GetMapping("/user/{userId}/total-balance")
    public ResponseEntity<Map<String, BigDecimal>> getTotalBalance(@PathVariable Long userId) {
        BigDecimal total = accountService.getTotalBalance(userId);
        return ResponseEntity.ok(Map.of("totalBalance", total));
    }
}
