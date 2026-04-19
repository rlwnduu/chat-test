package com.example.chat.channel.domain;

import com.example.chat.global.domain.BaseTimeEntity;
import com.example.chat.global.util.id.SnowflakeId;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PreRemove;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Channel extends BaseTimeEntity {

    @Id
    @SnowflakeId
    @Column(name = "channel_id")
    private Long id;

    @Column(length = 100)
    private String channelName;

    private int memberCount;

    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ChannelMember> members = new HashSet<>();

    private Instant deletedAt;

    @Builder
    public Channel(String channelName) {
        this.channelName = channelName;
        this.memberCount = 0;
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
