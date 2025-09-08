package com.kunfeng2002.be002.controller;

import java.util.List;
import java.util.Optional;

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
import com.kunfeng2002.be002.dto.response.UserDto;
import com.kunfeng2002.be002.entity.User;
import com.kunfeng2002.be002.repository.UserRepository;
import com.kunfeng2002.be002.service.TelegramBotService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final TelegramBotService telegramBotService;
    private final UserRepository userRepository;

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
            List<ChatMessageResponse> messages = telegramBotService.getRecentMessagesForUser(userAddress);
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

    @GetMapping("/telegram-status")
    public ResponseEntity<ResponseDTO> getTelegramStatus(@RequestParam String userAddress) {
        try {
            Long telegramUserId = telegramBotService.getTelegramUserIdByAddress(userAddress);
            if (telegramUserId != null) {
                return ResponseEntity.ok(ResponseDTO.builder()
                        .status(200)
                        .message("User is linked to Telegram")
                        .data("Linked to Telegram ID: " + telegramUserId)
                        .build());
            } else {
                return ResponseEntity.ok(ResponseDTO.builder()
                        .status(404)
                        .message("User is not linked to Telegram")
                        .data("Not linked")
                        .build());
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDTO.builder()
                    .status(400)
                    .message("Failed to check Telegram status: " + e.getMessage())
                    .build());
        }
    }

    @PostMapping("/refresh-user")
    public ResponseEntity<ResponseDTO> refreshUser(@RequestParam String userAddress) {
        try {
            Optional<User> userOpt = userRepository.findByWalletAddressQuery(userAddress);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                UserDto userDto = convertToDto(user);
                return ResponseEntity.ok(ResponseDTO.builder()
                        .status(200)
                        .message("User refreshed successfully")
                        .data(userDto)
                        .build());
            } else {
                return ResponseEntity.badRequest().body(ResponseDTO.builder()
                        .status(404)
                        .message("User not found")
                        .build());
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDTO.builder()
                    .status(400)
                    .message("Failed to refresh user: " + e.getMessage())
                    .build());
        }
    }

    private UserDto convertToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .telegramUserId(user.getTelegramUserId())
                .avatarUrl(user.getAvatarUrl())
                .bio(user.getBio())
                .isActive(user.getIsActive())
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .walletAddress(user.getWallet().getAddress())
                .build();
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
