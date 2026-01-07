package com.example.chat.message.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
public class MessageCreateRequest {

    private String content;

    public MessageCreateRequest() {
    }
}
