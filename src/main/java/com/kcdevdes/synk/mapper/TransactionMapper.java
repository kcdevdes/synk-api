package com.kcdevdes.synk.mapper;

import com.kcdevdes.synk.dto.request.TransactionCreateDTO;
import com.kcdevdes.synk.dto.request.TransactionUpdateDTO;
import com.kcdevdes.synk.dto.response.TransactionDTO;
import com.kcdevdes.synk.entity.TransactionEntity;
import com.kcdevdes.synk.entity.type.PaymentMethod;
import com.kcdevdes.synk.entity.type.TransactionType;
import com.kcdevdes.synk.util.InputSanitizer;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TransactionMapper {

    public static TransactionDTO toDTO(TransactionEntity entity) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(entity.getId());
        dto.setType(entity.getType() != null ? entity.getType().name() : null);
        dto.setAmount(entity.getAmount());
        dto.setMerchant(entity.getMerchant());
        dto.setCurrency(entity.getCurrency());
        dto.setDescription(entity.getDescription());
        dto.setCategory(entity.getCategory());
        dto.setTags(entity.getTags());
        dto.setPaymentMethod(entity.getPaymentMethod() != null ? entity.getPaymentMethod().name() : null);
        dto.setOccurredAt(entity.getOccurredAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setDeleted(entity.getDeleted());

        // 환율 정보
        dto.setOriginalAmount(entity.getOriginalAmount());
        dto.setOriginalCurrency(entity.getOriginalCurrency());
        dto.setExchangeRate(entity.getCurrencyExchangeRate());

        // Account 정보 (조인 데이터)
        if (entity.getAccount() != null) {
            dto.setAccountId(entity.getAccount().getId());
            dto.setAccountName(entity.getAccount().getAccountName());
        }

        return dto;
    }

    public static TransactionEntity toEntity(TransactionCreateDTO dto) {
        TransactionEntity entity = new TransactionEntity();

        // Type 변환 (String → Enum)
        entity.setType(TransactionType.valueOf(dto.getType()));

        entity.setAmount(dto.getAmount());
        entity.setMerchant(InputSanitizer.sanitizePlainText(dto.getMerchant(), "merchant"));
        entity.setCurrency(dto.getCurrency());
        entity.setCategory(InputSanitizer.sanitizePlainText(dto.getCategory(), "category"));
        entity.setDescription(InputSanitizer.sanitizePlainText(dto.getDescription(), "description"));
        entity.setTags(InputSanitizer.sanitizePlainText(dto.getTags(), "tags"));

        // PaymentMethod 변환 (String → Enum)
        if (dto.getPaymentMethod() != null) {
            entity.setPaymentMethod(PaymentMethod.valueOf(dto.getPaymentMethod()));
        }

        // 환율 정보
        entity.setOriginalAmount(dto.getOriginalAmount());
        entity.setOriginalCurrency(dto.getOriginalCurrency());
        entity.setCurrencyExchangeRate(dto.getExchangeRate());

        return entity;
    }

    public static void updateEntity(TransactionEntity entity, TransactionUpdateDTO dto) {
        if (dto.getAmount() != null) {
            entity.setAmount(dto.getAmount());
        }
        if (dto.getMerchant() != null) {
            entity.setMerchant(InputSanitizer.sanitizePlainText(dto.getMerchant(), "merchant"));
        }
        if (dto.getCategory() != null) {
            entity.setCategory(InputSanitizer.sanitizePlainText(dto.getCategory(), "category"));
        }
        if (dto.getDescription() != null) {
            entity.setDescription(InputSanitizer.sanitizePlainText(dto.getDescription(), "description"));
        }
        if (dto.getTags() != null) {
            entity.setTags(InputSanitizer.sanitizePlainText(dto.getTags(), "tags"));
        }
    }

    public static List<TransactionDTO> toDTOList(List<TransactionEntity> entities) {
        return entities.stream()
                .map(TransactionMapper::toDTO)
                .toList();
    }
}
