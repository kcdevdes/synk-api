package com.kcdevdes.synk.repository;

import com.kcdevdes.synk.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    // Authentication queries
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByUsername(String username);

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    // Password reset queries
    Optional<UserEntity> findByResetPasswordToken(String token);

    // Active user queries
    Optional<UserEntity> findByEmailAndDeletedFalse(String email);
    Optional<UserEntity> findByUsernameAndDeletedFalse(String username);
}
