package com.kunfeng2002.be002.dto.response;

public class GenerateBotResponse {
    private String botUsername;
    private String botToken;

    public GenerateBotResponse(String botUsername, String botToken) {
        this.botUsername = botUsername;
        this.botToken = botToken;
    }

    public String getBotUsername() {
        return botUsername;
    }

    public void setBotUsername(String botUsername) {
        this.botUsername = botUsername;
    }

    public String getBotToken() {
        return botToken;
    }

    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }
}
