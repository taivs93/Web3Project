package com.kunfeng2002.be002.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "followed_wallets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(FollowedWalletId.class)
public class FollowedWallet {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallets_id", nullable = false)
    private Wallet wallet;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tele_bot_id", nullable = false)
    private TeleBot teleBot;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
