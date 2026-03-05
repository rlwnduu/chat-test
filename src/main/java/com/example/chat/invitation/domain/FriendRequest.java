package com.example.chat.invitation.domain;

import com.example.chat.global.domain.BaseTimeEntity;
import com.example.chat.user.domain.User;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;

import java.time.Instant;

@Getter
@Entity
public class FriendRequest extends BaseTimeEntity {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "inviter_id")
    private User inviter;

    @ManyToOne
    @JoinColumn(name = "invitee_id")
    private User invitee;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    private Instant deletedAt;

    protected FriendRequest() {
    }

    public FriendRequest(User inviter, User invitee) {
        this.inviter = inviter;
        this.invitee = invitee;
        this.status = RequestStatus.PENDING;
    }

    public void accept() {
        this.status = RequestStatus.ACCEPTED;
    }

    public void decline() {
        this.status = RequestStatus.DECLINED;
    }
}
