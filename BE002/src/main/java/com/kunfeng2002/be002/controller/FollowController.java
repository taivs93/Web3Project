package com.kunfeng2002.be002.controller;

import com.kunfeng2002.be002.dto.response.ResponseDTO;
import com.kunfeng2002.be002.entity.FollowedWallet;
import com.kunfeng2002.be002.exception.FollowException;
import com.kunfeng2002.be002.exception.InvalidWalletAddressException;
import com.kunfeng2002.be002.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping
    public ResponseEntity<ResponseDTO> followAddress(
            @RequestParam String telegramId,
            @RequestParam String walletAddress) {
        
        followService.follow(telegramId, walletAddress);
        return ResponseEntity.ok(ResponseDTO.builder()
                .status(200)
                .message("Followed wallet successfully")
                .data("Followed: " + walletAddress)
                .build());
    }

    @DeleteMapping
    public ResponseEntity<ResponseDTO> unfollowAddress(
            @RequestParam String telegramId,
            @RequestParam String walletAddress) {
        
        followService.unfollow(telegramId, walletAddress);
        return ResponseEntity.ok(ResponseDTO.builder()
                .status(200)
                .message("Unfollowed wallet successfully")
                .data("Unfollowed: " + walletAddress)
                .build());
    }

    @GetMapping
    public ResponseEntity<ResponseDTO> getFollowedWallets(@RequestParam String telegramId) {
        List<FollowedWallet> followedWallets = followService.getFollowedWallets(telegramId);
        return ResponseEntity.ok(ResponseDTO.builder()
                .status(200)
                .message("Retrieved followed wallets successfully")
                .data(followedWallets)
                .build());
    }

    @GetMapping("/count")
    public ResponseEntity<ResponseDTO> getFollowCount(@RequestParam String telegramId) {
        long count = followService.getFollowCount(telegramId);
        return ResponseEntity.ok(ResponseDTO.builder()
                .status(200)
                .message("Retrieved follow count successfully")
                .data(count)
                .build());
    }
}

