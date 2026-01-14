package com.example.chat.message.service;

import com.example.chat.message.dto.MemberUpdateEventPayload;
import com.example.chat.message.dto.MessageCreateRequest;
import com.example.chat.message.dto.MessageEventPayload;
import com.example.chat.message.event.MessageSentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final SimpMessagingTemplate messagingTemplate;

    private final MessageService messageService;

    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void sendMessage(Long channelId, Long authorId, MessageCreateRequest request) {
        MessageEventPayload payload = messageService.createAndSaveMessage(channelId, authorId, request);
        eventPublisher.publishEvent(new MessageSentEvent(payload));
    }

    public void broadcastMemberUpdate(Long channelId, int memberCount) {
        MemberUpdateEventPayload responseDto = new MemberUpdateEventPayload(memberCount);
        messagingTemplate.convertAndSend(
                "/topic/channel." + channelId,
                responseDto
        );
    }
}
