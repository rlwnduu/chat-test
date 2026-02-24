package com.example.chat.global.redis;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum RedisTopic {
    CHAT_ROOM("chat:channel:"),
    USER_NOTIFICATION("user:notification:");

    private final String prefix;

    public String makeTopic(String id) {
        return this.prefix + id;
    }
}
