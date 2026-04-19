package com.example.chat.global.redis;

import com.example.chat.message.dto.MessageEventPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisPublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(MessageEventPayload payload) {
        String channelId = payload.getMessage().getChannelId();
        ChannelTopic topic = new ChannelTopic(RedisTopic.CHAT_ROOM.makeTopic(channelId));
        redisTemplate.convertAndSend(topic.getTopic(), payload);
    }
}
