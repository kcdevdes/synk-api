package com.kcdevdes.synk.mapper;

import com.kcdevdes.synk.dto.request.UserCreateDTO;
import com.kcdevdes.synk.dto.request.UserUpdateDTO;
import com.kcdevdes.synk.dto.response.UserDTO;
import com.kcdevdes.synk.entity.UserEntity;
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
        entity.setEmail(dto.getEmail());
        entity.setUsername(dto.getUsername());
        entity.setPassword(dto.getPassword()); // 주의: 실제로는 BCrypt 해싱 필요
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setMobile(dto.getMobile());
        entity.setDefaultCurrency(dto.getDefaultCurrency());
        entity.setLocale(dto.getLocale());
        entity.setTimezone(dto.getTimezone());

        return entity;
    }

    public static void updateEntity(UserEntity entity, UserUpdateDTO dto) {
        if (dto.getFirstName() != null) {
            entity.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            entity.setLastName(dto.getLastName());
        }
        if (dto.getMobile() != null) {
            entity.setMobile(dto.getMobile());
        }
        if (dto.getDefaultCurrency() != null) {
            entity.setDefaultCurrency(dto.getDefaultCurrency());
        }
        if (dto.getLocale() != null) {
            entity.setLocale(dto.getLocale());
        }
        if (dto.getTimezone() != null) {
            entity.setTimezone(dto.getTimezone());
        }
    }

    public static List<UserDTO> toDTOList(List<UserEntity> entities) {
        return entities.stream()
                .map(UserMapper::toDTO)
                .toList();
    }
}
