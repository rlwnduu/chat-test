package com.example.chat.message.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MessageSaveRequest {

    private Long channelId;
    private Long authorId;
    private String content;
}
