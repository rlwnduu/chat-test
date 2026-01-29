package com.example.chat.global.redis;

import com.example.chat.message.dto.MessageEventPayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisStreamService {

    private final RedisTemplate<String, Object> redisTemplate;

    // 모든 채팅 메시지를 처리하는 단일 Stream Key
    public static final String GLOBAL_STREAM_KEY = "chat:stream:global";

    /**
     * 채팅 메시지를 Redis Stream에 발행(Publish)합니다.
     * @param payload 전송할 메시지 데이터
     */
    public void publish(MessageEventPayload payload) {
        try {
            ObjectRecord<String, MessageEventPayload> record = StreamRecords.newRecord()
                    .ofObject(payload)
                    .withStreamKey(GLOBAL_STREAM_KEY);

            RecordId recordId = redisTemplate.opsForStream().add(record);
            
            log.debug("Published message to global stream. channelId: {} / RecordId: {}", payload.getChannelId(), recordId);

        } catch (Exception e) {
            log.error("Failed to publish message to Redis Stream. channelId: {}", payload.getChannelId(), e);
        }
    }
}
