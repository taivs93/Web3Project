package com.kunfeng2002.be002.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kunfeng2002.be002.dto.request.ChatMessageRequest;
import com.kunfeng2002.be002.dto.response.ChatMessageResponse;
import com.kunfeng2002.be002.dto.response.ResponseDTO;
import com.kunfeng2002.be002.service.TelegramBotService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final TelegramBotService telegramBotService;

    @PostMapping("/send")
    public ResponseEntity<ResponseDTO> sendMessage(@Valid @RequestBody ChatMessageRequest request) {
        try {
            ChatMessageResponse response = telegramBotService.processChatWebMessage(
                request.getMessage(),
                request.getUserAddress(),
                request.getChatId()
            );
            return ResponseEntity.ok(ResponseDTO.builder()
                    .status(200)
                    .message("Message processed successfully")
                    .data(response)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDTO.builder()
                    .status(400)
                    .message("Failed to process message: " + e.getMessage())
                    .build());
        }
    }

    @PostMapping("/link-telegram")
    public ResponseEntity<ResponseDTO> linkTelegram(
            @RequestParam String userAddress,
            @RequestParam Long telegramUserId,
            @RequestParam(required = false) String telegramUsername) {
        try {
            telegramBotService.linkUserToTelegram(userAddress, telegramUserId, telegramUsername);
            return ResponseEntity.ok(ResponseDTO.builder()
                    .status(200)
                    .message("Successfully linked to Telegram")
                    .data("Linked: " + userAddress + " → " + telegramUserId)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDTO.builder()
                    .status(400)
                    .message("Failed to link: " + e.getMessage())
                    .build());
        }
    }

    @GetMapping("/messages")
    public ResponseEntity<ResponseDTO> getTelegramMessages(@RequestParam String userAddress) {
        try {
            List<ChatMessageResponse> messages = telegramBotService.getTelegramMessages(userAddress);
            return ResponseEntity.ok(ResponseDTO.builder()
                    .status(200)
                    .message("Messages retrieved successfully")
                    .data(messages)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDTO.builder()
                    .status(400)
                    .message("Failed to get messages: " + e.getMessage())
                    .build());
        }
    }

    @GetMapping("/health")
    public ResponseEntity<ResponseDTO> chatHealth() {
        return ResponseEntity.ok(ResponseDTO.builder()
                .status(200)
                .message("Chat service is running!")
                .data("Web3 Chat Bot Active")
                .build());
    }

}
