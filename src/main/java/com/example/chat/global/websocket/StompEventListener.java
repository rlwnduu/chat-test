package com.example.chat.global.websocket;

import com.example.chat.global.redis.RedisSubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompEventListener {

    private final RedisSubscriptionService redisSubscriptionService;
    
    private final Map<String, String> subscriptionMap = new ConcurrentHashMap<>();

    @EventListener
    public void handleSubscribeEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = headerAccessor.getDestination();
        String sessionId = headerAccessor.getSessionId();
        String subscriptionId = headerAccessor.getSubscriptionId();

        if (destination != null && destination.startsWith("/topic/channel.")) {
            String channelId = destination.substring("/topic/channel.".length());

            String key = getSubscriptionKey(sessionId, subscriptionId);
            subscriptionMap.put(key, channelId);
            
            redisSubscriptionService.subscribe(channelId);
        }
    }

    @EventListener
    public void handleUnsubscribeEvent(SessionUnsubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String subscriptionId = headerAccessor.getSubscriptionId();
        
        String key = getSubscriptionKey(sessionId, subscriptionId);
        String channelId = subscriptionMap.remove(key);
        
        if (channelId != null) {
            redisSubscriptionService.unsubscribe(channelId);
        }
    }

    @EventListener
    public void handleDisconnectEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        
        subscriptionMap.entrySet().removeIf(entry -> {
            if (entry.getKey().startsWith(sessionId + ":")) {
                String channelId = entry.getValue();
                redisSubscriptionService.unsubscribe(channelId);
                return true;
            }
            return false;
        });
    }
    
    private String getSubscriptionKey(String sessionId, String subscriptionId) {
        return sessionId + ":" + subscriptionId;
    }
}
