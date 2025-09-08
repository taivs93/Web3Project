package com.kunfeng2002.be002.repository;

import com.kunfeng2002.be002.entity.User;
import com.kunfeng2002.be002.entity.Wallet;
import com.kunfeng2002.be002.entity.TeleBot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByWallet(Wallet wallet);

    @Query("SELECT u FROM User u JOIN u.wallet w WHERE w.address = :address")
    Optional<User> findByWalletAddressQuery(@Param("address") String address);

    @Query("SELECT u FROM User u JOIN u.teleBot t WHERE t.idTelegram = :telegramId")
    Optional<User> findByTelegramId(@Param("telegramId") String telegramId);

    @Query("SELECT u FROM User u WHERE u.teleBot.id = :teleBotId")
    Optional<User> findByTeleBotId(@Param("teleBotId") Long teleBotId);
}
