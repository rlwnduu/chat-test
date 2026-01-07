package com.example.chat.invitation.repository;

import com.example.chat.invitation.dto.ChannelInviteResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChannelInviteRepository extends JpaRepository<com.example.chat.invitation.domain.ChannelInvite, Long> {

    @Query("SELECT new com.example.chat.invitation.dto.ChannelInviteResponse(" +
            "    ci.id, " +
            "    c.id, " +
            "    c.channelName, " +
            "    new com.example.chat.user.dto.UserInfoResponse(i.id, i.username, i.nickname, i.profileImageUrl, i.profileIconColor), " +
            "    new com.example.chat.user.dto.UserInfoResponse(e.id, e.username, e.nickname, e.profileImageUrl, e.profileIconColor), " +
            "    ci.status, " +
            "    ci.createdAt" +
            ") " +
            "FROM ChannelInvite ci " +
            "JOIN ci.channel c " +
            "JOIN ci.inviter i " +
            "JOIN ci.invitee e " +
            "WHERE e.id = :userId " +
            "AND (:cursorId IS NULL OR ci.id < :cursorId) " +
            "ORDER BY ci.id DESC")
    Slice<ChannelInviteResponse> findInvitationsByInviteeIdWithCursor(
            @Param("userId") Long userId,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );
}
