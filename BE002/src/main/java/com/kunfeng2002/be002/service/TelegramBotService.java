package com.kunfeng2002.be002.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import static org.web3j.crypto.WalletUtils.isValidAddress;

import com.kunfeng2002.be002.Telegram.Bot;
import com.kunfeng2002.be002.Telegram.BotCommand;
import com.kunfeng2002.be002.dto.response.ChatMessageResponse;
import com.kunfeng2002.be002.entity.User;
import com.kunfeng2002.be002.entity.TeleBot;
import com.kunfeng2002.be002.exception.TelegramBotException;
import com.kunfeng2002.be002.repository.UserRepository;
import com.kunfeng2002.be002.repository.TeleBotRepository;
import com.kunfeng2002.be002.service.FollowService;

@Service
public class TelegramBotService {

    private final Bot bot;
    private final UserRepository userRepository;
    private final TeleBotRepository teleBotRepository;
    private final FollowService followService;
    private final Map<String, Long> userAddressToTelegramIdCache = new HashMap<>();

    private final Map<String, List<ChatMessageResponse>> telegramToWebMessages = new HashMap<>();

    public TelegramBotService(Bot bot, UserRepository userRepository, TeleBotRepository teleBotRepository, FollowService followService) {
        this.bot = bot;
        this.userRepository = userRepository;
        this.teleBotRepository = teleBotRepository;
        this.followService = followService;
    }

    private void sendToTelegram(Long userId, String... messages) {
        try {
            for (String msg : messages) {
                bot.sendText(userId, msg);
            }
        } catch (Exception e) {
            throw new TelegramBotException("Failed to send to Telegram: " + e.getMessage(), e);
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startBot() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            throw new TelegramBotException("Telegram API Error: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new TelegramBotException("Failed to start Telegram Bot: " + e.getMessage(), e);
        }
    }

    public String checkTeleLinkStatus(String telegramId) {
        Optional<User> userOpt = userRepository.findByTelegramId(telegramId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String walletAddress = user.getWallet().getAddress();
            userAddressToTelegramIdCache.put(walletAddress, Long.parseLong(telegramId));
            return walletAddress;
        }
        return null;
    }

    public ChatMessageResponse processWebChatMessage(String message, String userAddress, String sessionId) {

        String botResponse = generateBotResponse(message);
        boolean sentToTelegram = false;
        String telegramMessageId = null;

        Long telegramUserId = getTelegramUserIdByAddress(userAddress);
        if (telegramUserId != null) {
            sendToTelegram(telegramUserId, "Web Chat: " + message, "Bot: " + botResponse);
            sentToTelegram = true;
            telegramMessageId = "sent_" + System.currentTimeMillis();
        }

        return ChatMessageResponse.builder()
                .message(botResponse)
                .botName("Web3 Support Bot")
                .timestamp(System.currentTimeMillis())
                .isBot(true)
                .sessionId(sessionId)
                .sentToTelegram(sentToTelegram)
                .telegramMessageId(telegramMessageId)
                .build();
    }

    public ChatMessageResponse processChatWebMessage(String message, String userAddress, String sessionId) {
        String botResponse = this.generateBotResponse(message);

        boolean sentToTelegram = false;
        String telegramMessageId = null;

        Long telegramUserId = getTelegramUserIdByAddress(userAddress);
        if (telegramUserId != null) {
            sendToTelegram(telegramUserId, "Web: " + message, "Bot: " + botResponse);
            sentToTelegram = true;
            telegramMessageId = "sent_" + System.currentTimeMillis();

            storeTelegramMessage(userAddress, "Web: " + message, true);
            storeTelegramMessage(userAddress, "Bot: " + botResponse, false);
        }

        return ChatMessageResponse.builder()
                .message(botResponse)
                .botName("Web3 Support Bot")
                .timestamp(System.currentTimeMillis())
                .isBot(true)
                .sessionId(sessionId)
                .sentToTelegram(sentToTelegram)
                .telegramMessageId(telegramMessageId)
                .build();
    }

    public String generateBotResponse(String userText) {
        String text = userText.toLowerCase();

        if (BotCommand.isCommand(userText)) {
            Optional<BotCommand> commandOpt = BotCommand.fromText(userText);
            if (commandOpt.isPresent()) {
                BotCommand command = commandOpt.get();
                return generateCommandResponse(command);
            }
        }
        return "Re Send <- OR -> /help to view all command";
    }

    private String generateCommandResponse(BotCommand command) {
        return switch (command) {
            case HELP -> BotCommand.getAllCommandsHelp();
            case STATUS -> "Trạng thái: Bot bình thường!";
            case START -> "Chào! Tôi là KunFeng2002's Bot. /help to view all command";
            case MENU -> "Menu:\n\n" + BotCommand.getAllCommandsHelp();
            default -> "đang laod: " + command.getDescription();
        };
    }

    public Long getTelegramUserIdByAddress(String userAddress) {
        if (userAddressToTelegramIdCache.containsKey(userAddress)) {
            return userAddressToTelegramIdCache.get(userAddress);
        }

        Optional<User> userOpt = userRepository.findByWalletAddressQuery(userAddress);
        if (userOpt.isPresent()) {
            String telegramId = userOpt.get().getTeleBot().getIdTelegram();
            if (telegramId != null && !telegramId.equals("unlinked")) {
                Long telegramUserId = Long.parseLong(telegramId);
                userAddressToTelegramIdCache.put(userAddress, telegramUserId);
                return telegramUserId;
            }
        }

        return null;
    }

    public List<ChatMessageResponse> getTelegramMessages(String userAddress) {
        List<ChatMessageResponse> messages = telegramToWebMessages.getOrDefault(userAddress, new ArrayList<>());
        telegramToWebMessages.put(userAddress, new ArrayList<>());
        return messages;
    }

    public String findUserAddressByTelegramId(Long telegramUserId) {
        for (Map.Entry<String, Long> entry : userAddressToTelegramIdCache.entrySet()) {
            if (entry.getValue().equals(telegramUserId)) {
                return entry.getKey();
            }
        }

        Optional<User> userOpt = userRepository.findByTelegramUserId(telegramUserId);
        if (userOpt.isPresent()) {
            String walletAddress = userOpt.get().getWallet().getAddress();
            userAddressToTelegramIdCache.put(walletAddress, telegramUserId);
            return walletAddress;
        }

        return null;
    }

    @Transactional
    public void linkUserToTelegram(String userAddress, Long telegramUserId, String telegramUsername) {
        if (!isValidAddress(userAddress)) {
            throw new TelegramBotException("Invalid Ethereum address: " + userAddress);
        }

        User user = userRepository.findByWalletAddressQuery(userAddress)
                .orElseThrow(() -> new TelegramBotException("No user found for address " + userAddress));
        
        TeleBot teleBot = teleBotRepository.findByIdTelegram(telegramUserId.toString())
                .orElseGet(() -> {
                    TeleBot newTeleBot = new TeleBot(telegramUserId.toString());
                    return teleBotRepository.save(newTeleBot);
                });
        
        user.setTeleBot(teleBot);
        userRepository.save(user);

        userAddressToTelegramIdCache.put(userAddress, telegramUserId);

        try {
            bot.sendText(telegramUserId, "Successfully linked with address: " + userAddress + "\nYou can now chat with the bot!");
        } catch (Exception e) {
            throw new TelegramBotException("Failed to send confirmation message: " + e.getMessage(), e);
        }
    }

    private final Map<String, List<ChatMessageResponse>> recentTelegramMessages = new HashMap<>();

    public void storeTelegramMessage(String userAddress, String message, boolean isFromUser) {
        if (!recentTelegramMessages.containsKey(userAddress)) {
            recentTelegramMessages.put(userAddress, new ArrayList<>());
        }

        List<ChatMessageResponse> messages = recentTelegramMessages.get(userAddress);

        ChatMessageResponse telegramMessage = ChatMessageResponse.builder()
                .message(message)
                .botName(isFromUser ? "Telegram User" : "Telegram Bot")
                .timestamp(System.currentTimeMillis())
                .isBot(!isFromUser)
                .sessionId("telegram_sync")
                .sentToTelegram(true)
                .build();

        messages.add(telegramMessage);

        if (messages.size() > 50) {
            messages.remove(0);
        }
    }

    public List<ChatMessageResponse> getRecentMessagesForUser(String userAddress) {
        List<ChatMessageResponse> messages = recentTelegramMessages.getOrDefault(userAddress, new ArrayList<>());
        recentTelegramMessages.put(userAddress, new ArrayList<>());
        return new ArrayList<>(messages);
    }

    public void clearMessagesForUser(String userAddress) {
        recentTelegramMessages.put(userAddress, new ArrayList<>());
    }

    public String findWalletIdByTelegramId(Long telegramUserId) {
        for (Map.Entry<String, Long> entry : userAddressToTelegramIdCache.entrySet()) {
            if (entry.getValue().equals(telegramUserId)) {
                return entry.getKey();
            }
        }

        Optional<User> userOpt = userRepository.findByTelegramUserId(telegramUserId);
        if (userOpt.isPresent()) {
            String walletAddress = userOpt.get().getWallet().getAddress();
            userAddressToTelegramIdCache.put(walletAddress, telegramUserId);
            return walletAddress;
        }

        return null;
    }

}
