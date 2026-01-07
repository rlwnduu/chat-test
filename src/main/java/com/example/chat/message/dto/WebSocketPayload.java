package com.example.chat.message.dto;

import lombok.Getter;

@Getter
public class WebSocketPayload {

    private final PayloadType type;

    public WebSocketPayload(PayloadType type) {
        this.type = type;
    }
}
