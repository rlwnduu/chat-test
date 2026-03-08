package com.example.chat.channel.repository;

import com.example.chat.channel.domain.Channel;
import com.example.chat.channel.dto.ChannelSummaryProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChannelRepository extends JpaRepository<Channel, Long> {

    boolean existsById(Long channelId);

    @Query("SELECT c FROM Channel c " +
            "JOIN FETCH c.members cm " +
            "JOIN FETCH cm.user u " +
            "WHERE c.id = :channelId")
    Optional<Channel> findByIdWithMembersAndUsers(@Param("channelId") Long channelId);

    @Query("SELECT " +
            "   c.id AS id, " +
            "   c.channelName AS channelName, " +
            "   c.memberCount AS memberCount, " +
            "   cm.lastReadMessageId AS lastReadMessageId " +
            "FROM ChannelMember cm " +
            "JOIN cm.channel c " +
            "WHERE cm.user.id = :userId " +
            "  AND (:cursorId IS NULL OR cm.id < :cursorId) " +
            "ORDER BY cm.id DESC")
    Slice<ChannelSummaryProjection> findChannelProjectionsByUserId(
            @Param("userId") Long userId,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );
}
