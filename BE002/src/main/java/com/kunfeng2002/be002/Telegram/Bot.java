package com.kunfeng2002.be002.Telegram;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class Bot extends TelegramLongPollingBot {

    @Override
    public void onUpdateReceived(Update update) {
        var msg = update.getMessage();
        var user = msg.getFrom();

        System.out.println(user.getFirstName() + " wrote " + msg.getText());
    }

    @Override
    public String getBotUsername() {
        return "buildweb3_bot";
    }

    @Override
    public String getBotToken() {
        return "8431553398:AAFZkRvL-DJPQboQplPnQVXNOYaoKZa4pxE";
    }
}