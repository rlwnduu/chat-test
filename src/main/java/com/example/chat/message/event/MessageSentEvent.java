package com.example.chat.message.event;

import com.example.chat.message.dto.MessageEventPayload;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MessageSentEvent {

    private final MessageEventPayload payload;
}
