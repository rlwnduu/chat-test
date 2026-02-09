package com.example.chat.channel.domain;

import com.example.chat.user.domain.User;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.Instant;

@Getter
@EqualsAndHashCode
@Entity
@Table(
        name = "channel_member",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_channel_member_channel_id_user_id",
                        columnNames = {"channel_id", "user_id"}
                )
        },
        indexes = @Index(name = "idx_user_last_msg", columnList = "user_id, last_message_id DESC")
)
@SQLDelete(sql = "UPDATE channel_member SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class ChannelMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    private Instant createdAt;

    private Long lastMessageId;

    @Column(name = "last_read_message_id")
    private Long lastReadMessageId;

    private Instant deletedAt;

    protected ChannelMember() {
    }

    public ChannelMember(Channel channel, User user) {
        this.channel = channel;
        this.user = user;
    }

    public void softDelete() {
        this.deletedAt = Instant.now();
    }
}
