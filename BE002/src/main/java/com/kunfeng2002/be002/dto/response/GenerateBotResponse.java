package com.kunfeng2002.be002.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GenerateBotResponse {
    private String botUsername;
    private String botToken;

}
