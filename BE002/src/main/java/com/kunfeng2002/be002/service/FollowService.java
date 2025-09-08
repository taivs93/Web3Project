package com.kunfeng2002.be002.service;

import com.kunfeng2002.be002.dto.request.FollowRequest;
import com.kunfeng2002.be002.entity.FollowedWallet;
import com.kunfeng2002.be002.entity.TeleBot;
import com.kunfeng2002.be002.entity.Wallet;
import com.kunfeng2002.be002.exception.FollowException;
import com.kunfeng2002.be002.exception.InvalidWalletAddressException;
import com.kunfeng2002.be002.repository.FollowedWalletRepository;
import com.kunfeng2002.be002.repository.TeleBotRepository;
import com.kunfeng2002.be002.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static org.web3j.crypto.WalletUtils.isValidAddress;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowedWalletRepository followedWalletRepository;
    private final TeleBotRepository teleBotRepository;
    private final WalletRepository walletRepository;

    @Transactional
    public void follow(String telegramId, String walletAddress) {
        validateInput(telegramId, walletAddress);
        
        TeleBot teleBot = findOrCreateTeleBot(telegramId);
        Wallet wallet = findOrCreateWallet(walletAddress);
        
        if (followedWalletRepository.existsByTeleBotIdAndWalletId(teleBot.getId(), wallet.getId())) {
            throw new FollowException("Wallet already followed");
        }
        
        FollowedWallet followedWallet = FollowedWallet.builder()
                .teleBot(teleBot)
                .wallet(wallet)
                .build();

        followedWalletRepository.save(followedWallet);
    }

    @Transactional
    public void unfollow(String telegramId, String walletAddress) {
        validateInput(telegramId, walletAddress);
        
        TeleBot teleBot = teleBotRepository.findByIdTelegram(telegramId)
                .orElseThrow(() -> new FollowException("Telegram bot not found"));
        
        Wallet wallet = walletRepository.findByAddress(walletAddress)
                .orElseThrow(() -> new FollowException("Wallet not found"));

        FollowedWallet followedWallet = followedWalletRepository
                .findByTeleBotIdAndWalletId(teleBot.getId(), wallet.getId())
                .orElseThrow(() -> new FollowException("Wallet not being followed"));
        
        followedWalletRepository.delete(followedWallet);
    }

    public List<FollowedWallet> getFollowedWallets(String telegramId) {
        if (telegramId == null || telegramId.trim().isEmpty()) {
            throw new FollowException("Telegram ID cannot be null or empty");
        }
        
        TeleBot teleBot = teleBotRepository.findByIdTelegram(telegramId)
                .orElseThrow(() -> new FollowException("Telegram bot not found"));
        
        return followedWalletRepository.findByTeleBotId(teleBot.getId());
    }

    public long getFollowCount(String telegramId) {
        if (telegramId == null || telegramId.trim().isEmpty()) {
            throw new FollowException("Telegram ID cannot be null or empty");
        }
        
        TeleBot teleBot = teleBotRepository.findByIdTelegram(telegramId)
                .orElseThrow(() -> new FollowException("Telegram bot not found"));
        
        return followedWalletRepository.countByTeleBotId(teleBot.getId());
    }
    
    private void validateInput(String telegramId, String walletAddress) {
        if (telegramId == null || telegramId.trim().isEmpty()) {
            throw new FollowException("Telegram ID cannot be null or empty");
        }
        
        if (walletAddress == null || walletAddress.trim().isEmpty()) {
            throw new FollowException("Wallet address cannot be null or empty");
        }
        
        if (!isValidAddress(walletAddress)) {
            throw new InvalidWalletAddressException("Invalid wallet address format: " + walletAddress);
        }
    }
    
    private TeleBot findOrCreateTeleBot(String telegramId) {
        return teleBotRepository.findByIdTelegram(telegramId)
                .orElseGet(() -> {
                    TeleBot newTeleBot = new TeleBot(telegramId);
                    return teleBotRepository.save(newTeleBot);
                });
    }
    
    private Wallet findOrCreateWallet(String walletAddress) {
        return walletRepository.findByAddress(walletAddress)
                .orElseGet(() -> {
                    Wallet newWallet = new Wallet(walletAddress);
                    return walletRepository.save(newWallet);
                });
    }
}
