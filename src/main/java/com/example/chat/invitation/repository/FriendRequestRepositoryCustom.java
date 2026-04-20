package com.example.chat.invitation.repository;

import com.example.chat.invitation.dto.FriendRequestProjection;
import com.example.chat.invitation.dto.InviteSearchCondition;

import java.util.List;

public interface FriendRequestRepositoryCustom {
    List<FriendRequestProjection> search(InviteSearchCondition condition);
}
