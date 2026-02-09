package com.example.chat.invitation.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ChannelInviteRequest {

    private Long channelId;
    private Long inviteeId;

    protected ChannelInviteRequest() {
    }

    public ChannelInviteRequest(Long channelId, Long inviteeId) {
        this.channelId = channelId;
        this.inviteeId = inviteeId;
    }
}

