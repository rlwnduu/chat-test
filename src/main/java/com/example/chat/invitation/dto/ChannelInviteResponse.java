package com.example.chat.invitation.dto;

import com.example.chat.invitation.domain.ChannelInvite;
import com.example.chat.invitation.domain.RequestStatus;
import com.example.chat.user.dto.UserInfoResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;

@Getter
@ToString
public class ChannelInviteResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long channelId;

    private String channelName;

    private UserInfoResponse inviter;

    private UserInfoResponse invitee;

    private RequestStatus status;

    private Instant createdAt;

    protected ChannelInviteResponse() {
    }

    public ChannelInviteResponse(
            ChannelInvite channelInvite,
            UserInfoResponse inviter, UserInfoResponse invitee) {
        this.id = channelInvite.getId();
        this.channelId = channelInvite.getChannel().getId();
        this.channelName = channelInvite.getChannel().getChannelName();
        this.inviter = inviter;
        this.invitee = invitee;
        this.status = channelInvite.getStatus();
        this.createdAt = channelInvite.getCreatedAt();
    }

    public ChannelInviteResponse(
            Long id,
            Long channelId,
            String channelName,
            UserInfoResponse inviter,
            UserInfoResponse invitee,
            RequestStatus status,
            Instant createdAt
    ) {
        this.id = id;
        this.channelId = channelId;
        this.channelName = channelName;
        this.inviter = inviter;
        this.invitee = invitee;
        this.status = status;
        this.createdAt = createdAt;
    }
}
