package com.example.chat.user.domain;

import com.example.chat.channel.domain.ChannelMember;
import com.example.chat.global.util.id.SnowflakeId;
import com.example.chat.user.dto.UserCreateRequest;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "users")
@SQLDelete(sql = "UPDATE users SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class User {

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

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "id")
    private List<Authority> authorities = new ArrayList<>();

    private int friendCount;

    @OneToMany(mappedBy = "user")
    private List<ChannelMember> channelMembers;

    private int channelInviteCount;

    private int friendRequestCount;

    @CreationTimestamp
    private Instant createdAt;

    private Instant deletedAt;

    public User() {
    }

    public User(String loginId,
                String password,
                String username,
                String nickname,
                String profileIconColor
    ) {
        this.loginId = loginId;
        this.password = password;
        this.username = username;
        this.nickname = nickname;
        this.profileIconColor = profileIconColor;
    }

    public static User create(UserCreateRequest userCreateRequest,
                              String encodedPassword,
                              String initialUsername,
                              String initialProfileIconColor) {
        User user = new User();
        user.loginId = userCreateRequest.getLoginId();
        user.password = encodedPassword;
        user.username = initialUsername;
        user.nickname = initialUsername;
        user.profileImageUrl = null;
        user.profileIconColor = initialProfileIconColor;
        user.status = UserStatus.ACTIVE;
        return user;
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
