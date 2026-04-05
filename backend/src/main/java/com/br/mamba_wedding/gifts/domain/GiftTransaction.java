package com.br.mamba_wedding.gifts.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "gift_transactions")
public class GiftTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gift_id", nullable = false)
    private Gift gift;

    @Column(nullable = false, length = 120)
    private String guestName;

    @Column(nullable = false)
    private Integer numberQuotas;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private TransactionStatus status;

    private LocalDateTime reservedAt;
    private LocalDateTime reservedUntil;
    private LocalDateTime purchasedAt;
}