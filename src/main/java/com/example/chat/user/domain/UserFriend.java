package com.example.chat.user.domain;

import com.example.chat.global.domain.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
@Table(
        name = "user_friend",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_friend_users",
                        columnNames = {"user_a_id", "user_b_id"}
                )
        }
)
public class UserFriend extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_a_id", nullable = false)
    private User userA;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_b_id", nullable = false)
    private User userB;

    public UserFriend(User user1, User user2) {
        if (user1.getId().equals(user2.getId())) {
            throw new IllegalArgumentException("User cannot be friends with themselves.");
        }

        if (user1.getId() < user2.getId()) {
            this.userA = user1;
            this.userB = user2;
        } else {
            this.userA = user2;
            this.userB = user1;
        }

        userA.incrementFriendCount();
        userB.incrementFriendCount();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                "{" +
                "id=" + id +
                ", userAId=" + (userA != null ? userA.getId() : "null") +
                ", userBId=" + (userB != null ? userB.getId() : "null") +
                "}";
    }
}