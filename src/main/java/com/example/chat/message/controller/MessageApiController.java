package com.example.chat.message.controller;

import com.example.chat.message.dto.MessageLoadResponse;
import com.example.chat.message.service.MessageService;
import com.example.chat.global.security.user.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MessageApiController {

    private final MessageService messageService;

    @GetMapping("/messages")
    public ResponseEntity<MessageLoadResponse> getMessages(
            @RequestParam Long channelId,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        MessageLoadResponse response = messageService.getMessages(
                channelId,
                userDetails.getId(),
                cursor,
                size
        );
        return ResponseEntity.ok(response);
    }
}

