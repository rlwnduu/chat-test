package com.example.chat.message.dto;

import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;

public interface MessageView {

    @Value("#{target.id}")
    String getMessageId();

    String getChannelId();

    String getAuthorId();

    String getContent();

    Instant getCreatedAt();
}
