package com.example.chat.global.redis;

import com.example.chat.message.dto.MessageEventPayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messagingTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String publishMessage = new String(message.getBody());
            MessageEventPayload payload = objectMapper.readValue(publishMessage, MessageEventPayload.class);

            Long channelId = payload.getChannelId();
            log.debug("Redis Pub/Sub Received. channelId: {}", channelId);

            messagingTemplate.convertAndSend("/topic/channel." + channelId, payload);
        } catch (Exception e) {
            log.error("Failed to handle Redis Pub/Sub message", e);
        }
    }
}
