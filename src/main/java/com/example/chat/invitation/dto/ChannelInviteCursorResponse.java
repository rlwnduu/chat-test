package com.example.chat.invitation.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ChannelInviteCursorResponse {

    private List<ChannelInviteResponse> channelInvites;

    private String nextCursor;

    private boolean hasNext;

    public ChannelInviteCursorResponse(
            List<ChannelInviteResponse> channelInvites,
            String nextCursor,
            boolean hasNext
    ) {
        this.channelInvites = channelInvites;
        this.nextCursor = nextCursor;
        this.hasNext = hasNext;
    }
}
