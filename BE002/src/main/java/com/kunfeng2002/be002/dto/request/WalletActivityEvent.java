package com.kunfeng2002.be002.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WalletActivityEvent {
    private String txHash;
    private String from;
    private String to;
    private String network;
    private String value;
}
