package com.example.chat.message.dto;

import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MessageSaveRequest {

    private Long channelId;
    private Long authorId;
    private String content;
}
