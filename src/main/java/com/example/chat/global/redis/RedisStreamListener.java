package com.example.chat.global.redis;

import com.example.chat.message.dto.MessageCreateRequest;
import com.example.chat.message.dto.MessageEventPayload;
import com.example.chat.message.dto.MessageSaveRequest;
import com.example.chat.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisStreamListener implements StreamListener<String, ObjectRecord<String, MessageSaveRequest>> {

    private final MessageService messageService;
    private final RedisPublisher redisPublisher;

    @Override
    public void onMessage(ObjectRecord<String, MessageSaveRequest> message) {
        try {
            MessageSaveRequest request = message.getValue();

            MessageCreateRequest createRequest = new MessageCreateRequest(request.getContent());
            MessageEventPayload payload = messageService.createAndSaveMessage(
                    request.getChannelId(),
                    request.getAuthorId(),
                    createRequest
            );

            redisPublisher.publish(payload);
        } catch (Exception e) {
            log.error("Failed to process message from Redis Stream", e);
        }
    }
}
