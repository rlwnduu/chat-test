package com.example.chat.invitation.domain;

import com.example.chat.user.domain.User;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FriendRequest extends BaseInvitation {

    public FriendRequest(User inviter, User invitee) {
        super(inviter, invitee);
    }
}
