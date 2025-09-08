package com.kunfeng2002.be002.entity;

import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FollowedWalletId implements Serializable {

    private Long wallet;
    private Long teleBot;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FollowedWalletId that = (FollowedWalletId) o;
        return Objects.equals(wallet, that.wallet) && Objects.equals(teleBot, that.teleBot);
    }

    @Override
    public int hashCode() {
        return Objects.hash(wallet, teleBot);
    }
}
