package com.example.chat.message.controller;

import com.example.chat.channel.service.ChannelService;
import com.example.chat.global.security.user.StompPrincipal;
import com.example.chat.message.dto.MessageCreateRequest;
import com.example.chat.message.dto.MessageReadRequest;
import com.example.chat.message.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChannelService channelService;
    private final ChatService chatService;

    @MessageMapping("/channel/{channelId}/message")
    public void sendMessage(
            @DestinationVariable Long channelId,
            @Payload MessageCreateRequest messageCreateRequest,
            StompPrincipal principal
    ) {
        Long userId = Long.parseLong(principal.getName());
        chatService.sendMessage(channelId, userId, messageCreateRequest);
    }

    @MessageMapping("/channel/{channelId}/read")
    public void markAsRead(@DestinationVariable Long channelId,
                           @Payload MessageReadRequest request,
                           StompPrincipal principal
    ) {
        Long userId = Long.parseLong(principal.getName());
        channelService.markAsRead(channelId, userId, request.getLastReadMessageId());
    }
}
