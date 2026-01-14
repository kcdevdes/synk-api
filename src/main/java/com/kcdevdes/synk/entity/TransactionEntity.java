package com.kcdevdes.synk.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "transactions")
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 128)
    private String merchant;

    /// Currency Details ///

    @Column(nullable = false)
    private Long userid; // UserEntity와 @ManyToOne 관게

    @Column(nullable = false, length = 3) // ISO 4217
    private String currency = "USD";

    @Column(precision = 15, scale = 2)
    private BigDecimal originalAmount;

    @Column(length = 3)
    private String originalCurrency; // Example: "KRW" -> USD

    @Column
    private BigDecimal currencyExchangeRate;

    /// Payment ///

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private PaymentMethod paymentMethod;

    @Column
    private Long accountId; // AccountEntity 연결

    ///  TimeStamps ///

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant occurredAt;

    @UpdateTimestamp
    private Instant updatedAt;

    @Column
    private Instant deletedAt;

    @Column(nullable = false) // Soft Deletion
    private Boolean deleted = false;

    /// Metadata ///

    @Column(length = 256)
    private String tags; // Comma separated tags

    @Column(length = 512)
    private String description;

    @Column(length = 64)
    private String category;
}
