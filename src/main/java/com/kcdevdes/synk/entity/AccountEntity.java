package com.kcdevdes.synk.entity;

import com.kcdevdes.synk.entity.type.AccountType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "accounts")
@Getter
@Setter
public class AccountEntity {

    /** Core **/

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 128)
    private String accountName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType accountType;

    @Column(nullable = false, length = 3)
    private String currency = "USD";

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;

    /** Account Details **/

    @Column(length = 32)
    private String accountNumber;

    @Column(length = 32)
    private String bankName;

    @Column(length = 512)
    private String description;

    /** Status **/
    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false)
    private Boolean deleted = false;

    /** Timestamps **/
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    @Column
    private Instant deletedAt;

    @Column
    private Instant lastTransactionAt;

    /** Relations **/

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @OneToMany(mappedBy = "account")
    private List<TransactionEntity> transactions;

    /** Utility Methods **/

    public void deposit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        this.balance = this.balance.subtract(amount);
    }

    public String getFormattedBalance() {
        return String.format("%s %,.2f", currency, balance);
    }

    public String getMaskedAccountNumber() {
        if (this.accountNumber == null || accountNumber.length() < 4) return "****";
        return "****" + accountNumber.substring(accountNumber.length() - 4);
    }
}
