package com.kunfeng2002.be002.dto.response;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;

@Getter
@Builder
public class ChatMessageResponse {
    
    private String message;
    private String botName;
    private Long timestamp;

    @Value("is_bot")
    private boolean isBot;

    @Value("session_id")
    private String sessionId;

    @Value("sent_to_telegram")
    private boolean sentToTelegram;

    @Value("telegram_message_id")
    private String telegramMessageId;
}
