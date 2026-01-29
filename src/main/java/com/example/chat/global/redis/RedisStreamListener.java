package com.example.chat.global.redis;

import com.example.chat.message.dto.MessageEventPayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisStreamListener implements StreamListener<String, ObjectRecord<String, MessageEventPayload>> {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(ObjectRecord<String, MessageEventPayload> message) {
        try {
            MessageEventPayload payload = message.getValue();
            Long channelId = payload.getChannelId();

            log.debug("Received message from stream. channelId: {}, messageId: {}", channelId, payload.getMessageId());

            messagingTemplate.convertAndSend("/sub/channel/" + channelId, payload);

        } catch (Exception e) {
            log.error("Failed to process message from Redis Stream", e);
        }
    }
}
