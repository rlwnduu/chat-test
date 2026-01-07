package com.example.chat.channel.domain;

import com.example.chat.channel.dto.ChannelCreateRequest;
import com.example.chat.global.util.id.SnowflakeId;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Table(indexes = @Index(name = "idx_last_message_id", columnList = "lastMessageId DESC"))
@SQLDelete(sql = "UPDATE channel SET deleted_at = NOW() WHERE channel_id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Channel {

    @Id
    @SnowflakeId
    @Column(name = "channel_id")
    private Long id;

    @Column(length = 100)
    private String channelName;

    private int memberCount;

    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ChannelMember> members;

    private Long lastMessageId;

    @Column(length = 100)
    private String lastMessageContent;

    private Instant lastMessageAt;

    private Instant createdAt;

    private Instant deletedAt;

    public Channel() {
        this.members = new HashSet<>();
    }

    public static Channel from(ChannelCreateRequest channelCreateRequest) {
        Channel channel = new Channel();
        channel.channelName = channelCreateRequest.getChannelName();
        channel.createdAt = Instant.now();
        return channel;
    }

    public void incrementMemberCount() {
        this.memberCount++;
    }

    @PreRemove
    private void preRemove() {
        for (ChannelMember member : members) {
            member.softDelete();
        }
    }
}
