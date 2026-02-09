package com.example.chat.channel.repository;

import com.example.chat.channel.domain.ChannelMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChannelMemberRepository extends JpaRepository<ChannelMember, Long>, ChannelMemberRepositoryCustom {

    boolean existsByChannelIdAndUserId(Long channelId, Long userId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE ChannelMember cm " +
            "SET cm.lastReadMessageId = :lastReadMessageId " +
            "WHERE cm.channel.id = :channelId " +
            "  AND cm.user.id = :userId " +
            "  AND (cm.lastReadMessageId IS NULL OR cm.lastReadMessageId < :lastReadMessageId)")
    int updateLastReadMessageIdSafe(
            @Param("channelId") Long channelId,
            @Param("userId") Long userId,
            @Param("lastReadMessageId") Long lastReadMessageId
    );

    @Modifying
    @Query("UPDATE ChannelMember cm SET cm.lastMessageId = :msgId WHERE cm.channel.id = :channelId")
    void updateLastMessageId(@Param("channelId") Long channelId, @Param("msgId") Long msgId);
}
