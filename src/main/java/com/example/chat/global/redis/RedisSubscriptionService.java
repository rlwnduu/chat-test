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

    private final Map<String, AtomicInteger> topicSubscribers = new ConcurrentHashMap<>();
    private final Map<String, MessageListenerAdapter> activeListeners = new ConcurrentHashMap<>();

    public void subscribe(String topicName) {
        AtomicInteger counter = topicSubscribers.computeIfAbsent(topicName, k -> new AtomicInteger(0));
        int currentCount = counter.incrementAndGet();

        if (currentCount == 1) {
            startRedisSubscription(topicName);
        }
    }

    public void unsubscribe(String topicName) {
        AtomicInteger counter = topicSubscribers.get(topicName);
        if (counter == null) return;

        int currentCount = counter.decrementAndGet();

        if (currentCount <= 0) {
            stopRedisSubscription(topicName);
            topicSubscribers.remove(topicName);
        }
    }

    private void startRedisSubscription(String topicName) {
        ChannelTopic topic = new ChannelTopic(topicName);

        MessageListenerAdapter listenerAdapter = new MessageListenerAdapter(redisSubscriber, "onMessage");
        listenerAdapter.afterPropertiesSet();

        redisMessageListenerContainer.addMessageListener(listenerAdapter, topic);
        activeListeners.put(topicName, listenerAdapter);
    }

    private void stopRedisSubscription(String topicName) {
        ChannelTopic topic = new ChannelTopic(topicName);

        MessageListenerAdapter listenerAdapter = activeListeners.remove(topicName);
        if (listenerAdapter != null) {
            redisMessageListenerContainer.removeMessageListener(listenerAdapter, topic);
        }
    }
}
