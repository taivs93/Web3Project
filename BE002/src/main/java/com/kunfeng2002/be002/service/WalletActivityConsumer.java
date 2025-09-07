package com.kunfeng2002.be002.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalletActivityConsumer {

    @KafkaListener(topics = "wallet-activity", groupId = "wallet-notifier")
    public void consume(String message) {
        System.out.println("Received wallet activity: " + message);
    }
}

