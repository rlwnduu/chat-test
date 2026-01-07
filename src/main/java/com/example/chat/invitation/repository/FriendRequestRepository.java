package com.example.chat.invitation.repository;

import com.example.chat.invitation.domain.FriendRequest;
import com.example.chat.invitation.domain.RequestStatus;
import com.example.chat.invitation.dto.FriendRequestResponse;
import com.example.chat.user.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    @Query("SELECT new com.example.chat.invitation.dto.FriendRequestResponse(" +
            "    fr.id, " +
            "    new com.example.chat.user.dto.UserInfoResponse(i.id, i.username, i.nickname, i.profileImageUrl, i.profileIconColor), " +
            "    new com.example.chat.user.dto.UserInfoResponse(e.id, e.username, e.nickname, e.profileImageUrl, e.profileIconColor), " +
            "    fr.status, " +
            "    fr.createdAt" +
            ") " +
            "FROM FriendRequest fr " +
            "JOIN fr.inviter i " +
            "JOIN fr.invitee e " +
            "WHERE e.id = :userId " +
            "AND fr.status = :status " +  // [추가됨] 상태 조건 추가
            "AND (:cursorId IS NULL OR fr.id < :cursorId) " +
            "ORDER BY fr.id DESC")
    Slice<FriendRequestResponse> findRequestsByInviteeIdAndStatusWithCursor(
            @Param("userId") Long userId,
            @Param("status") RequestStatus status, // [추가됨] Enum 파라미터
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );

    boolean existsByInviterAndInviteeAndStatus(User inviter, User invitee, RequestStatus status);
}
