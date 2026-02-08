package com.example.chat.message.service;

import com.example.chat.global.redis.RedisStreamService;
import com.example.chat.message.dto.MemberUpdateEventPayload;
import com.example.chat.message.dto.MessageCreateRequest;
import com.example.chat.message.dto.MessageSaveRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final SimpMessagingTemplate messagingTemplate;

    private final RedisStreamService redisStreamService;

    public void sendMessage(Long channelId, Long authorId, MessageCreateRequest request) {
        MessageSaveRequest saveRequest = new MessageSaveRequest(channelId, authorId, request.getContent());
        redisStreamService.publish(saveRequest);
    }

    public void broadcastMemberUpdate(Long channelId, int memberCount) {
        MemberUpdateEventPayload responseDto = new MemberUpdateEventPayload(memberCount);
        messagingTemplate.convertAndSend(
                "/topic/channel." + channelId,
                responseDto
        );
    }
}
