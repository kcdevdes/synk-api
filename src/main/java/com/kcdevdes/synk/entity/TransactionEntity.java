package com.kcdevdes.synk.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "transactions")
@Getter
@Setter
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TransactionType type;
    private Double amount;

    @CreationTimestamp
    private Instant occurredAt;
    @UpdateTimestamp
    private Instant updatedAt;

    private String merchant;
    private String description;

    @Override
    public String toString() {
        return "TransactionEntity{" +
                "id=" + id +
                ", type=" + type +
                ", amount=" + amount +
                ", occurredAt=" + occurredAt +
                ", updatedAt=" + updatedAt +
                ", merchant='" + merchant + '\'' +
                ", description='" + description + '\'' +
                '}';
    }


}
