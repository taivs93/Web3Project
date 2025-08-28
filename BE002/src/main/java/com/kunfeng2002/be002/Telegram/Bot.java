package com.kunfeng2002.be002.Telegram;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class Bot extends TelegramLongPollingBot {

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println("Hello, I'm KunFeng's bot!");
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