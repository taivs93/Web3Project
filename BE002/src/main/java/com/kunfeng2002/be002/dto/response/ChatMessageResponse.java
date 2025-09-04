package com.kunfeng2002.be002.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {
    
    private String message;
    private String botName;
    private Long timestamp;
    private boolean isBot;
    private String sessionId;
    
    // Status của việc gửi tin nhắn
    private boolean sentToTelegram;
    private String telegramMessageId;
}
