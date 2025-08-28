package com.kunfeng2002.be002.Telegram;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class Bot extends TelegramLongPollingBot {

    // Error ------------------------------
    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.token}")
    private String botToken;
    //-------------------------------------

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println("Hello, I'm KunFeng's bot!");
        var msg = update.getMessage();
        var user = msg.getFrom();

        System.out.println(user.getFirstName() + " " + user.getLastName() + " wrote " + msg.getText());
    }

    @Override
    public String getBotUsername() {
//         return "buildweb3_bot";
        return botUsername;
    }

    @Override
    public String getBotToken() {
//         return "8431553398:AAFZkRvL-DJPQboQplPnQVXNOYaoKZa4pxE";
        return botToken;
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
}