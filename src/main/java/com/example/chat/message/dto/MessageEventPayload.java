package com.example.chat.message.dto;

import com.example.chat.message.domain.Message;
import com.example.chat.user.dto.UserInfoProjection;
import com.example.chat.user.dto.UserInfoResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;

@Getter
@ToString
public class MessageEventPayload extends WebSocketPayload{

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long messageId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long channelId;

    private UserInfoProjection author;

    private String content;

    private Instant createdAt;

    public MessageEventPayload(Message message, UserInfoProjection author) {
        super(PayloadType.MESSAGE);
        this.messageId = message.getId();
        this.channelId = message.getChannelId();
        this.author = author;
        this.content = message.getContent();
        this.createdAt = message.getCreatedAt();
    }
}
