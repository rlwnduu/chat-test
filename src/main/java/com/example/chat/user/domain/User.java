package com.example.chat.user.domain;

import com.example.chat.channel.domain.ChannelMember;
import com.example.chat.global.domain.BaseTimeEntity;
import com.example.chat.global.util.id.SnowflakeId;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PreRemove;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@ToString(exclude = {"password", "authorities", "channelMembers"})
@Table(name = "users")
@SQLDelete(sql = "UPDATE users SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class User extends BaseTimeEntity {

    @Id
    @SnowflakeId
    private Long id;

    @Column(nullable = false, unique = true)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String nickname;

    private String profileImageUrl;

    private String profileIconColor;

    private UserStatus status;

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Authority> authorities = new HashSet<>();

    private int friendCount;

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private Set<ChannelMember> channelMembers = new HashSet<>();

    private int channelInviteCount;

    private int friendRequestCount;

    private Instant deletedAt;

    public void addRole(Role role) {
        Authority authority = new Authority(this, role);
        this.authorities.add(authority);
    }

    public void removeRole(Role role) {
        this.authorities.removeIf(auth -> auth.getRole() == role);
    }

    public void incrementFriendCount() {
        this.friendCount++;
    }

    public void decrementFriendCount() {
        this.friendCount--;
    }

    public void incrementChannelInviteCount() {
        this.channelInviteCount++;
    }

    public void decrementChannelInviteCount() {
        this.channelInviteCount--;
    }

    public void incrementFriendRequestCount() {
        this.friendRequestCount++;
    }

    public void decrementFriendRequestCount() {
        this.friendRequestCount--;
    }

    @PreRemove
    private void preRemove() {
    }
}
