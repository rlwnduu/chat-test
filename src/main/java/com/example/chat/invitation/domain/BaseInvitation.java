package com.example.chat.invitation.domain;

import com.example.chat.global.domain.BaseTimeEntity;
import com.example.chat.global.util.id.SnowflakeId;
import com.example.chat.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@MappedSuperclass
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseInvitation extends BaseTimeEntity {

    @Id
    @SnowflakeId
    private Long id;

    @ManyToOne
    @JoinColumn(name = "inviter_id", nullable = false)
    private User inviter;

    @ManyToOne
    @JoinColumn(name = "invitee_id", nullable = false)
    private User invitee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvitationStatus status;

    protected BaseInvitation(User inviter, User invitee) {
        this.inviter = inviter;
        this.invitee = invitee;
        this.status = InvitationStatus.PENDING;
    }

    public void accept() {
        this.status = InvitationStatus.ACCEPTED;
    }

    public void decline() {
        this.status = InvitationStatus.DECLINED;
    }

    @PrePersist
    protected void prePersist() {
        if (this.status == null) {
            this.status = InvitationStatus.PENDING;
        }
    }
}
