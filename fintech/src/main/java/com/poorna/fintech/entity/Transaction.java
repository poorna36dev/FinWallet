package com.poorna.fintech.entity;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "type", nullable = false)
    private String type;
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;
    @Column(name = "currency", nullable = false)
    private String currency;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_wallet")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Wallet destinationWallet;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_wallet")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Wallet sourceWallet;
    @Column(name = "status")
    private String status;
    @CreationTimestamp
    private LocalDateTime createdAt;
}
