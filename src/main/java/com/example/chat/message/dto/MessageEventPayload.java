package com.example.chat.message.dto;

import com.example.chat.user.dto.UserInfoResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageEventPayload extends WebSocketPayload {

    private MessageResponse message;
    private UserInfoResponse author;

    @Builder
    public MessageEventPayload(MessageResponse message, UserInfoResponse author) {
        super(PayloadType.MESSAGE);
        this.message = message;
        this.author = author;
    }
}
