package com.example.chat.message.dto;

import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MessageReadRequest {

    private Long lastReadMessageId;
}
