package com.example.chat.invitation.repository;

import com.example.chat.invitation.domain.FriendRequest;
import com.example.chat.invitation.domain.InvitationStatus;
import com.example.chat.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long>, FriendRequestRepositoryCustom {

    boolean existsByInviterAndInviteeAndStatus(User inviter, User invitee, InvitationStatus status);
}
