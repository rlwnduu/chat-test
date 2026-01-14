package com.example.chat.channel.repository;

import com.example.chat.channel.domain.Channel;
import com.example.chat.channel.dto.ChannelSummaryProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface ChannelRepository extends JpaRepository<Channel, Long> {

    boolean existsById(Long channelId);

    @Query("SELECT c FROM Channel c " +
            "JOIN FETCH c.members cm " +
            "JOIN FETCH cm.user u " +
            "WHERE c.id = :channelId")
    Optional<Channel> findByIdWithMembersAndUsers(@Param("channelId") Long channelId);

    @Query("SELECT cm.channel FROM ChannelMember cm " +
            "WHERE cm.user.id = :userId AND (:cursor IS NULL OR cm.channel.lastMessageAt < :cursor) " +
            "ORDER BY cm.channel.lastMessageAt DESC")
    Slice<Channel> findByUserIdWithCursor(@Param("userId") Long userId,
                                          @Param("cursor") Instant cursor,
                                          Pageable pageable);

    @Query("SELECT NEW com.example.chat.channel.dto.ChannelSummaryProjection(" +
            "   c.id, " +
            "   c.channelName, " +
            "   c.memberCount, " +
            "   cm.lastMessageId, " + // [변경] Channel 대신 ChannelMember 값 사용
            "   c.lastMessageContent, " +
            "   c.lastMessageAt, " +
            "   cm.lastReadMessageId " +
            ") " +
            "FROM ChannelMember cm " +
            "JOIN cm.channel c " +
            "WHERE cm.user.id = :userId " +
            "  AND (:cursorId IS NULL OR cm.lastMessageId < :cursorId) " + // [변경] 커서 조건도 cm 기준
            "ORDER BY cm.lastMessageId DESC")
        // [핵심] 정렬 기준 변경
    Slice<ChannelSummaryProjection> findChannelProjectionsByUserId(
            @Param("userId") Long userId,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );

    @Modifying
    @Query("UPDATE Channel c " +
            "SET c.lastMessageId = :messageId, " +
            "    c.lastMessageContent = :content, " +
            "    c.lastMessageAt = :createdAt " +
            "WHERE c.id = :channelId")
    void updateLastMessage(
            @Param("channelId") Long channelId,
            @Param("messageId") Long messageId,
            @Param("content") String content,
            @Param("createdAt") Instant createdAt
    );
}
