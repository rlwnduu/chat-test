package com.example.chat.invitation.domain;

import com.example.chat.channel.domain.Channel;
import com.example.chat.user.domain.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChannelInvite extends BaseInvitation {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;

    public ChannelInvite(User inviter, User invitee, Channel channel) {
        super(inviter, invitee);
        this.channel = channel;
    }
}
