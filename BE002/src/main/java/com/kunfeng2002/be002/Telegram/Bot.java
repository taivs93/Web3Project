package com.kunfeng2002.be002.Telegram;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.CopyMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.kunfeng2002.be002.service.TelegramBotService;
import java.util.Optional;

@Component
public class Bot extends TelegramLongPollingBot {

    private final String botUsername;
    private final String botToken;
    private final ApplicationContext applicationContext;

    @Autowired
    public Bot(@Value("${telegram.bot.username}") String botUsername,
               @Value("${telegram.bot.token}") String botToken,
               ApplicationContext applicationContext) {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.applicationContext = applicationContext;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        var msg = update.getMessage();
        var user = msg.getFrom();
        var username = user.getUserName();
        var idTele = user.getId();

        TelegramBotService telegramBotService = applicationContext.getBean(TelegramBotService.class);

        if (msg.isCommand()) {
            handleCommand(msg.getText(), idTele, username, telegramBotService);
            return;
        }

        String userAddress = telegramBotService.findWalletIdByTelegramId(idTele);

        if (userAddress != null) {
            telegramBotService.storeTelegramMessage(userAddress, msg.getText(), true);
            String botResponse = telegramBotService.generateBotResponse(msg.getText());
            sendText(idTele, botResponse);
            telegramBotService.storeTelegramMessage(userAddress, botResponse, false);
        } else {
            sendText(idTele, "Link address with first /link_YourAddress");
        }

        System.out.println(user.getFirstName() + " " + user.getLastName() + " - " + username + " - " + idTele + " wrote " + msg.getText());
    }


    public void copyMessage(Long who, Integer msgId){
        CopyMessage cm = CopyMessage.builder()
                .fromChatId(who.toString())
                .chatId(who.toString())
                .messageId(msgId)
                .build();
        try {
            execute(cm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendText(Long who, String what) {
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString())
                .text(what).build();
        try {
            execute(sm);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean isValidAddress(String address) {
        return address != null && address.matches("^0x[a-fA-F0-9]{40}$");
    }

    private void handleCommand(String commandText, Long telegramUserId, String username, TelegramBotService telegramBotService) {
        Optional<BotCommand> commandOpt = BotCommand.fromText(commandText);

        if (commandOpt.isEmpty()) {
            sendText(telegramUserId, "Unknown command. Use /help to see available commands.");
            return;
        }

        BotCommand command = commandOpt.get();

        switch (command) {
            case START -> handleStartCommand(telegramUserId, telegramBotService);
            case HELP -> sendText(telegramUserId, BotCommand.getAllCommandsHelp());
            case LINK -> handleLinkCommand(commandText, telegramUserId, username, telegramBotService);
            case STATUS -> handleStatusCommand(telegramUserId, telegramBotService);
            case MENU -> sendText(telegramUserId, "Menu:\n\n" + BotCommand.getAllCommandsHelp());
            case SETTINGS -> sendText(telegramUserId, "Settings: Coming soon");
            case HISTORY -> sendText(telegramUserId, "Transaction History: Coming soon");
            case FOLLOW -> sendText(telegramUserId, "Follow Wallet: Coming soon");
            case UNFOLLOW -> sendText(telegramUserId, "Unfollow Wallet: Coming soon");
            case PRICE -> sendText(telegramUserId, "Token Prices: Coming soon");
            case BALANCE -> sendText(telegramUserId, "Balance Check: Coming soon");
            default -> sendText(telegramUserId, "Command not implemented yet.");
        }
    }

    private void handleStartCommand(Long telegramUserId, TelegramBotService telegramBotService) {
        String linkAddress = telegramBotService.checkTeleLinkStatus(telegramUserId);
        if (linkAddress != null) {
            sendText(telegramUserId, "Linked address: " + linkAddress + "\n\nWelcome! /help to see available commands.");
        } else {
            sendText(telegramUserId, "You need to link to wallet address first.\n\nUse: /link_YourWalletAddress");
        }
    }

    private void handleLinkCommand(String commandText, Long telegramUserId, String username, TelegramBotService telegramBotService) {
        String walletAddress = commandText.substring(6).trim();
        if (isValidAddress(walletAddress)) {
            telegramBotService.linkUserToTelegram(walletAddress, telegramUserId, username);
        } else {
            sendText(telegramUserId, "Address is not valid. Please use format: /link_0x...");
        }
    }

    private void handleStatusCommand(Long telegramUserId, TelegramBotService telegramBotService) {
        String linkAddress = telegramBotService.checkTeleLinkStatus(telegramUserId);
        if (linkAddress != null) {
            sendText(telegramUserId, "Status: Connected\nAddress: " + linkAddress);
        } else {
            sendText(telegramUserId, "Status: Not connected\nPlease link your wallet first.");
        }
    }

}