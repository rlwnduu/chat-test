package com.example.chat.invitation.dto;

import com.example.chat.channel.dto.ChannelInfoProjection;
import com.example.chat.invitation.domain.InvitationStatus;
import com.example.chat.user.dto.UserInfoProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class ChannelInviteProjection {
    private Long id;
    private ChannelInfoProjection channel;
    private UserInfoProjection inviter;
    private UserInfoProjection invitee;
    private InvitationStatus status;
    private Instant createdAt;
}
