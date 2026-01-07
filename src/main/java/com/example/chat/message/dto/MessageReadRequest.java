package com.example.chat.message.dto;

import lombok.Getter;

@Getter
public class MessageReadRequest {

    private Long lastReadMessageId;

    public MessageReadRequest() {
    }

    public MessageReadRequest(Long lastReadMessageId) {
        this.lastReadMessageId = lastReadMessageId;
    }
}
