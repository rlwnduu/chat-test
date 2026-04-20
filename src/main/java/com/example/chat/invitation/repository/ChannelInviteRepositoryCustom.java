package com.example.chat.invitation.repository;

import com.example.chat.invitation.dto.ChannelInviteProjection;
import com.example.chat.invitation.dto.InviteSearchCondition;

import java.util.List;

public interface ChannelInviteRepositoryCustom {

    List<ChannelInviteProjection> search(InviteSearchCondition condition);
}
