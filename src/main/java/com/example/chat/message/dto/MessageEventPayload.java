package com.example.chat.message.dto;

import com.example.chat.message.domain.Message;
import com.example.chat.user.dto.UserInfoProjection;
import com.example.chat.user.dto.UserInfoResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;

@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageEventPayload extends WebSocketPayload {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long messageId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long channelId;

    private UserInfoResponse author;

    private String content;

    private Instant createdAt;

    public MessageEventPayload() {
        super(PayloadType.MESSAGE);
    }

    public MessageEventPayload(Message message, UserInfoProjection authorProjection) {
        super(PayloadType.MESSAGE);
        this.messageId = message.getId();
        this.channelId = message.getChannelId();
        this.content = message.getContent();
        this.createdAt = message.getCreatedAt();
        
        this.author = new UserInfoResponse(
                Long.parseLong(authorProjection.getId()),
                authorProjection.getUsername(),
                authorProjection.getNickname(),
                authorProjection.getProfileImageUrl(),
                authorProjection.getProfileIconColor()
        );
    }
}
