package com.kcdevdes.synk.mapper;

import com.kcdevdes.synk.dto.request.AccountCreateDTO;
import com.kcdevdes.synk.dto.request.AccountUpdateDTO;
import com.kcdevdes.synk.dto.response.AccountDTO;
import com.kcdevdes.synk.entity.AccountEntity;
import com.kcdevdes.synk.entity.type.AccountType;
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

        entity.setAccountName(dto.getAccountName());
        entity.setCurrency(dto.getCurrency());
        entity.setBalance(dto.getBalance());
        entity.setAccountNumber(dto.getAccountNumber());
        entity.setBankName(dto.getBankName());
        entity.setDescription(dto.getDescription());

        return entity;
    }

    public static void updateEntity(AccountEntity entity, AccountUpdateDTO dto) {
        if (dto.getAccountName() != null) {
            entity.setAccountName(dto.getAccountName());
        }
        if (dto.getBalance() != null) {
            entity.setBalance(dto.getBalance());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
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
