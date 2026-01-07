package com.example.chat.message.dto;

import lombok.Getter;

@Getter
public class MemberUpdateEventPayload extends WebSocketPayload{

    private int memberCount;

    public MemberUpdateEventPayload(int memberCount) {
        super(PayloadType.ALERT_MEMBER_UPDATE);
        this.memberCount = memberCount;
    }
}
