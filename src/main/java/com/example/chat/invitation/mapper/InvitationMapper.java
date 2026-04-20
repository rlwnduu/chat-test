package com.example.chat.invitation.mapper;

import com.example.chat.invitation.domain.ChannelInvite;
import com.example.chat.invitation.domain.FriendRequest;
import com.example.chat.invitation.dto.ChannelInviteProjection;
import com.example.chat.invitation.dto.ChannelInviteResponse;
import com.example.chat.invitation.dto.FriendRequestProjection;
import com.example.chat.invitation.dto.FriendRequestResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InvitationMapper {

    ChannelInviteResponse toResponse(ChannelInvite entity);

    FriendRequestResponse toResponse(FriendRequest entity);

    ChannelInviteResponse toResponse(ChannelInviteProjection projection);

    FriendRequestResponse toResponse(FriendRequestProjection projection);

    List<ChannelInviteResponse> toChannelInviteResponseList(List<ChannelInviteProjection> projections);

    List<FriendRequestResponse> toFriendRequestResponseList(List<FriendRequestProjection> projections);
}
