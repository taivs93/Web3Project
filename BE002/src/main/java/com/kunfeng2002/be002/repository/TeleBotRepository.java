package com.kunfeng2002.be002.repository;

import com.kunfeng2002.be002.entity.TeleBot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeleBotRepository extends JpaRepository<TeleBot, Long> {

    Optional<TeleBot> findByIdTelegram(String idTelegram);

    @Query("SELECT t FROM TeleBot t WHERE t.idTelegram = :idTelegram")
    Optional<TeleBot> findByTelegramId(@Param("idTelegram") String idTelegram);

    boolean existsByIdTelegram(String idTelegram);
}
