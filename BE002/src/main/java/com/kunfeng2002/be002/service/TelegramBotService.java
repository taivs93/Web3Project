package com.kunfeng2002.be002.service;

import com.kunfeng2002.be002.Telegram.Bot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.updates.DeleteWebhook;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class TelegramBotService {

    @Autowired
    private Bot bot;

    @EventListener(ApplicationReadyEvent.class)
    public void startBot() {
        try {

            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
//            Bot bot = new Bot();
            botsApi.registerBot(new Bot());
            // bot.sendText(user.getId(), "Hello World!");

        } catch (TelegramApiException e) {
            System.err.println("Telegram API Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Failed to start Telegram Bot: " + e.getMessage());
        }
    }
}
