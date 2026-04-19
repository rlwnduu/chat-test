package com.example.chat.message.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class MessageResponse {
    private String messageId;
    private String channelId;
    private String authorId;
    private String content;
    private Instant createdAt;
}
