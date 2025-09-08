package com.kunfeng2002.be002.repository;

import com.kunfeng2002.be002.entity.FollowedWallet;
import com.kunfeng2002.be002.entity.FollowedWalletId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowedWalletRepository extends JpaRepository<FollowedWallet, FollowedWalletId> {

    @Query("SELECT fw FROM FollowedWallet fw WHERE fw.teleBot.id = :teleBotId")
    List<FollowedWallet> findByTeleBotId(@Param("teleBotId") Long teleBotId);

    @Query("SELECT fw FROM FollowedWallet fw WHERE fw.wallet.id = :walletId")
    List<FollowedWallet> findByWalletId(@Param("walletId") Long walletId);

    @Query("SELECT fw FROM FollowedWallet fw WHERE fw.teleBot.id = :teleBotId AND fw.wallet.id = :walletId")
    Optional<FollowedWallet> findByTeleBotIdAndWalletId(@Param("teleBotId") Long teleBotId, @Param("walletId") Long walletId);

    boolean existsByTeleBotIdAndWalletId(Long teleBotId, Long walletId);

    @Query("SELECT COUNT(fw) FROM FollowedWallet fw WHERE fw.teleBot.id = :teleBotId")
    long countByTeleBotId(@Param("teleBotId") Long teleBotId);

    @Query("SELECT COUNT(fw) FROM FollowedWallet fw WHERE fw.wallet.id = :walletId")
    long countByWalletId(@Param("walletId") Long walletId);
}
