package com.example.chat.global.redis;

import com.example.chat.message.dto.MessageSaveRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisStreamService {

    public static final String GLOBAL_STREAM_KEY = "chat:stream:global";

    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(MessageSaveRequest request) {
        try {
            ObjectRecord<String, MessageSaveRequest> record = StreamRecords.newRecord()
                    .ofObject(request)
                    .withStreamKey(GLOBAL_STREAM_KEY);

            redisTemplate.opsForStream().add(record);
        } catch (Exception e) {
            log.error("Failed to publish save request to Redis Stream. channelId: {}", request.getChannelId(), e);
        }
    }
}
