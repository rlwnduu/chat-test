package com.example.chat.global.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisSubscriptionService {

    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final RedisSubscriber redisSubscriber;

    private final Map<String, AtomicInteger> channelSubscribers = new ConcurrentHashMap<>();
    private final Map<String, MessageListenerAdapter> activeListeners = new ConcurrentHashMap<>();

    public void subscribe(String channelId) {
        AtomicInteger counter = channelSubscribers.computeIfAbsent(channelId, k -> new AtomicInteger(0));
        int currentCount = counter.incrementAndGet();

        if (currentCount == 1) {
            startRedisSubscription(channelId);
        }
    }

    public void unsubscribe(String channelId) {
        AtomicInteger counter = channelSubscribers.get(channelId);
        if (counter == null) return;

        int currentCount = counter.decrementAndGet();

        if (currentCount <= 0) {
            stopRedisSubscription(channelId);
            channelSubscribers.remove(channelId);
        }
    }

    private void startRedisSubscription(String channelId) {
        String topicName = "chat:channel:" + channelId;
        ChannelTopic topic = new ChannelTopic(topicName);

        MessageListenerAdapter listenerAdapter = new MessageListenerAdapter(redisSubscriber, "onMessage");
        listenerAdapter.afterPropertiesSet();

        redisMessageListenerContainer.addMessageListener(listenerAdapter, topic);
        activeListeners.put(channelId, listenerAdapter);
    }

    private void stopRedisSubscription(String channelId) {
        String topicName = "chat:channel:" + channelId;
        ChannelTopic topic = new ChannelTopic(topicName);

        MessageListenerAdapter listenerAdapter = activeListeners.remove(channelId);
        if (listenerAdapter != null) {
            redisMessageListenerContainer.removeMessageListener(listenerAdapter, topic);
        }
    }
}
