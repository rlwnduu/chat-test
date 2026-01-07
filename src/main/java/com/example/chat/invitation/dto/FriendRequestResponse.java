package com.example.chat.invitation.dto;

import com.example.chat.invitation.domain.RequestStatus;
import com.example.chat.user.dto.UserInfoResponse;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;

@Getter
@ToString
public class FriendRequestResponse {

    private Long id;

    private UserInfoResponse inviter;

    private UserInfoResponse invitee;

    private RequestStatus status;

    private Instant createdAt;

    public FriendRequestResponse() {
    }

    public FriendRequestResponse(Long id, UserInfoResponse inviter, UserInfoResponse invitee, RequestStatus status, Instant createdAt) {
        this.id = id;
        this.inviter = inviter;
        this.invitee = invitee;
        this.status = status;
        this.createdAt = createdAt;
    }
}
