package com.kunfeng2002.be002.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "tele_bot")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeleBot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_telegram", nullable = false, length = 45)
    private String idTelegram;

    @OneToMany(mappedBy = "teleBot", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<User> users;

    @OneToMany(mappedBy = "teleBot", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FollowedWallet> followedWallets;

    public TeleBot(String idTelegram) {
        this.idTelegram = idTelegram;
    }
}
