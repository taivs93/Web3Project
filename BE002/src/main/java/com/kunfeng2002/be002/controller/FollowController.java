package com.kunfeng2002.be002.controller;

import com.kunfeng2002.be002.dto.request.FollowRequest;
import com.kunfeng2002.be002.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping
    public ResponseEntity<Void> followAddress(@RequestBody FollowRequest request) {
        followService.follow(request);
        return ResponseEntity.ok().build();
    }
}

