package com.kcdevdes.synk.mapper;

import com.kcdevdes.synk.dto.request.AccountCreateDTO;
import com.kcdevdes.synk.dto.request.AccountUpdateDTO;
import com.kcdevdes.synk.dto.response.AccountDTO;
import com.kcdevdes.synk.entity.AccountEntity;
import com.kcdevdes.synk.entity.type.AccountType;
import com.kcdevdes.synk.util.InputSanitizer;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AccountMapper {

    public static AccountDTO toDTO(AccountEntity entity) {
        AccountDTO dto = new AccountDTO();
        dto.setId(entity.getId());
        dto.setAccountName(entity.getAccountName());
        dto.setAccountType(entity.getAccountType() != null ? entity.getAccountType().name() : null);
        dto.setCurrency(entity.getCurrency());
        dto.setBalance(entity.getBalance());
        dto.setAccountNumber(entity.getAccountNumber());
        dto.setBankName(entity.getBankName());
        dto.setDescription(entity.getDescription());
        dto.setActive(entity.getActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setLastTransactionAt(entity.getLastTransactionAt());

        return dto;
    }

    public static AccountEntity toEntity(AccountCreateDTO dto) {
        AccountEntity entity = new AccountEntity();

        // AccountType 변환 (String → Enum)
        entity.setAccountType(AccountType.valueOf(dto.getAccountType()));

        entity.setAccountName(InputSanitizer.sanitizePlainText(dto.getAccountName(), "accountName"));
        entity.setCurrency(dto.getCurrency());
        entity.setBalance(dto.getBalance());
        entity.setAccountNumber(InputSanitizer.sanitizePlainText(dto.getAccountNumber(), "accountNumber"));
        entity.setBankName(InputSanitizer.sanitizePlainText(dto.getBankName(), "bankName"));
        entity.setDescription(InputSanitizer.sanitizePlainText(dto.getDescription(), "description"));

        return entity;
    }

    public static void updateEntity(AccountEntity entity, AccountUpdateDTO dto) {
        if (dto.getAccountName() != null) {
            entity.setAccountName(InputSanitizer.sanitizePlainText(dto.getAccountName(), "accountName"));
        }
        if (dto.getBalance() != null) {
            entity.setBalance(dto.getBalance());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(InputSanitizer.sanitizePlainText(dto.getDescription(), "description"));
        }
        if (dto.getActive() != null) {
            entity.setActive(dto.getActive());
        }
    }

    public static List<AccountDTO> toDTOList(List<AccountEntity> entities) {
        return entities.stream()
                .map(AccountMapper::toDTO)
                .toList();
    }
}
