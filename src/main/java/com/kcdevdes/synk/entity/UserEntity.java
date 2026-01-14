package com.kcdevdes.synk.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_email", columnList = "email"),
        @Index(name = "idx_username", columnList = "username")
}) // Index 성능 높이기
@Getter
@Setter
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**  Authentication **/

    @Column(unique = true, nullable = false, length = 128)
    private String email;

    @Column(unique = true, nullable = false, length = 128)
    private String username;

    @Column(nullable = false)
    private String password;

    /** Profile **/

    @Column(length = 64)
    private String firstName;

    @Column(length = 64)
    private String lastName;

    @Column(length = 32)
    private String mobile;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    /**  Preference **/

    @Column(nullable = false, length = 3)
    private String defaultCurrency = "USD";

    @Column(nullable = false, length = 10)
    private String locale = "en-US";

    @Column(nullable = false, length = 32)
    private String timezone = "America/New_York";

    /** Security **/

    @Column(nullable = false)
    private Boolean emailVerified = false;

    @Column(nullable = false)
    private Integer failedLoginAttempts = 0;

    @Column
    private Instant lockedUntil;

    @Column
    private String resetPasswordToken;

    @Column
    private Instant resetPasswordTokenExpiresAt;

    /** Account Status **/

    @Column
    private Boolean active = true;

    @Column
    private Instant lastLoginAt;

    @Column(nullable = false)
    private Boolean deleted = false;

    @Column
    private Instant deletedAt;

    /** Utility Methods **/

    public String getFormattedName(boolean reverse) {
        return reverse ? lastName + ", " + firstName : firstName + " " + lastName;
    }

    public Boolean isAccountLocked() {
        return lockedUntil != null && Instant.now().isBefore(lockedUntil);
    }

    public boolean canLogin() {
        return !isAccountLocked() && active && !deleted;
    }
}
