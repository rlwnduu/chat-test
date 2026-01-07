package com.example.chat.invitation.domain;

import com.example.chat.channel.domain.Channel;
import com.example.chat.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;

@Getter
@Entity
public class ChannelInvite {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "inviter_id")
    private User inviter;     // 초대를 보낸 사람

    @ManyToOne
    @JoinColumn(name = "invitee_id")
    private User invitee;  // 초대를 받은 사람

    @ManyToOne
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @Enumerated(EnumType.STRING)
    private RequestStatus status; // (PENDING, ACCEPTED, DECLINED)

    private Instant createdAt;

    private Instant updatedAt;

    private Instant deletedAt;

    protected ChannelInvite() {
    }

    public ChannelInvite(User inviter, User invitee, Channel channel) {
        this.inviter = inviter;
        this.invitee = invitee;
        this.channel = channel;
        this.status = RequestStatus.PENDING;
        this.createdAt = Instant.now();
    }

    public void accept() {
        this.status = RequestStatus.ACCEPTED;
        this.updatedAt = Instant.now();
    }

    public void decline() {
        this.status = RequestStatus.DECLINED;
        this.updatedAt = Instant.now();
    }
}
