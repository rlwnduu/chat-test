package com.example.chat.invitation.dto;

import com.example.chat.invitation.domain.InvitationStatus;
import com.example.chat.user.dto.UserInfoResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendRequestResponse {
    private String id;
    private UserInfoResponse inviter;
    private UserInfoResponse invitee;
    private InvitationStatus status;
    private Instant createdAt;
}
