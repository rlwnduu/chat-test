package com.example.chat.invitation.repository;

import com.example.chat.invitation.dto.ChannelInviteResponse;
import com.example.chat.invitation.dto.InviteSearchCondition;

import java.util.List;

public interface ChannelInviteRepositoryCustom {

    List<ChannelInviteResponse> search(InviteSearchCondition condition);
}
