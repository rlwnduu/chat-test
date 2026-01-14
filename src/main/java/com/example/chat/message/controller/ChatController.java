package com.example.chat.message.controller;

import com.example.chat.channel.service.ChannelService;
import com.example.chat.global.security.user.CustomUserDetails;
import com.example.chat.message.dto.MessageCreateRequest;
import com.example.chat.message.dto.MessageReadRequest;
import com.example.chat.message.service.ChatService;
import com.example.chat.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final UserRepository userRepository;
    private final ChannelService channelService;
    private final ChatService chatService;

    @MessageMapping("/channel/{channelId}/message")
    public void sendMessage(
            @DestinationVariable Long channelId,
            @Payload MessageCreateRequest messageCreateRequest,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getId();
        chatService.sendMessage(channelId, userId, messageCreateRequest);
    }

    @MessageMapping("/channel/{channelId}/read")
    public void markAsRead(@DestinationVariable Long channelId,
                           @Payload MessageReadRequest request,
                           @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getId();
        channelService.markAsRead(channelId, userId, request.getLastReadMessageId());
    }
}
