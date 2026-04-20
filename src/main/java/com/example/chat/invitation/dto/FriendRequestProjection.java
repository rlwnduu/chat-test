package com.example.chat.invitation.dto;

import com.example.chat.invitation.domain.InvitationStatus;
import com.example.chat.user.dto.UserInfoProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class FriendRequestProjection {
    private Long id;
    private UserInfoProjection inviter;
    private UserInfoProjection invitee;
    private InvitationStatus status;
    private Instant createdAt;
}
