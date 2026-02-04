package com.kcdevdes.synk.mapper;

import com.kcdevdes.synk.dto.request.UserCreateDTO;
import com.kcdevdes.synk.dto.request.UserUpdateDTO;
import com.kcdevdes.synk.dto.response.UserDTO;
import com.kcdevdes.synk.entity.UserEntity;
import com.kcdevdes.synk.util.InputSanitizer;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {

    public static UserDTO toDTO(UserEntity entity) {
        UserDTO dto = new UserDTO();
        dto.setId(entity.getId());
        dto.setEmail(entity.getEmail());
        dto.setUsername(entity.getUsername());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setMobile(entity.getMobile());
        dto.setDefaultCurrency(entity.getDefaultCurrency());
        dto.setLocale(entity.getLocale());
        dto.setTimezone(entity.getTimezone());
        dto.setActive(entity.getActive());
        dto.setEmailVerified(entity.getEmailVerified());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setLastLoginAt(entity.getLastLoginAt());

        return dto;
    }

    public static UserEntity toEntity(UserCreateDTO dto) {
        UserEntity entity = new UserEntity();
        entity.setEmail(InputSanitizer.sanitizePlainText(dto.getEmail(), "email"));
        entity.setUsername(InputSanitizer.sanitizePlainText(dto.getUsername(), "username"));
        entity.setPassword(dto.getPassword()); // 주의: 실제로는 BCrypt 해싱 필요
        entity.setFirstName(InputSanitizer.sanitizePlainText(dto.getFirstName(), "firstName"));
        entity.setLastName(InputSanitizer.sanitizePlainText(dto.getLastName(), "lastName"));
        entity.setMobile(InputSanitizer.sanitizePlainText(dto.getMobile(), "mobile"));
        entity.setDefaultCurrency(dto.getDefaultCurrency());
        entity.setLocale(InputSanitizer.sanitizePlainText(dto.getLocale(), "locale"));
        entity.setTimezone(InputSanitizer.sanitizePlainText(dto.getTimezone(), "timezone"));

        return entity;
    }

    public static void updateEntity(UserEntity entity, UserUpdateDTO dto) {
        if (dto.getFirstName() != null) {
            entity.setFirstName(InputSanitizer.sanitizePlainText(dto.getFirstName(), "firstName"));
        }
        if (dto.getLastName() != null) {
            entity.setLastName(InputSanitizer.sanitizePlainText(dto.getLastName(), "lastName"));
        }
        if (dto.getMobile() != null) {
            entity.setMobile(InputSanitizer.sanitizePlainText(dto.getMobile(), "mobile"));
        }
        if (dto.getDefaultCurrency() != null) {
            entity.setDefaultCurrency(dto.getDefaultCurrency());
        }
        if (dto.getLocale() != null) {
            entity.setLocale(InputSanitizer.sanitizePlainText(dto.getLocale(), "locale"));
        }
        if (dto.getTimezone() != null) {
            entity.setTimezone(InputSanitizer.sanitizePlainText(dto.getTimezone(), "timezone"));
        }
    }

    public static List<UserDTO> toDTOList(List<UserEntity> entities) {
        return entities.stream()
                .map(UserMapper::toDTO)
                .toList();
    }
}
