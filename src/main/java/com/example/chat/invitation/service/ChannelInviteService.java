package com.example.chat.invitation.service;

import com.example.chat.channel.domain.Channel;
import com.example.chat.channel.domain.ChannelMember;
import com.example.chat.channel.repository.ChannelMemberRepository;
import com.example.chat.channel.repository.ChannelRepository;
import com.example.chat.global.dto.PageResponse;
import com.example.chat.global.error.BusinessException;
import com.example.chat.global.error.ErrorCode;
import com.example.chat.invitation.domain.ChannelInvite;
import com.example.chat.invitation.domain.RequestStatus;
import com.example.chat.invitation.dto.ChannelInviteRequest;
import com.example.chat.invitation.dto.ChannelInviteResponse;
import com.example.chat.invitation.dto.InviteSearchCondition;
import com.example.chat.invitation.repository.ChannelInviteRepository;
import com.example.chat.message.service.ChatService;
import com.example.chat.user.domain.User;
import com.example.chat.user.dto.UserInfoResponse;
import com.example.chat.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChannelInviteService {

    private final ChannelRepository channelRepository;
    private final ChannelMemberRepository channelMemberRepository;
    private final ChannelInviteRepository channelInviteRepository;
    private final UserRepository userRepository;
    private final ChatService chatService;

    @Transactional(readOnly = true)
    public PageResponse<ChannelInviteResponse> getChannelInvitations(InviteSearchCondition condition) {
        condition.setStatus(RequestStatus.PENDING);
        List<ChannelInviteResponse> content = channelInviteRepository.search(condition);

        boolean hasNext = content.size() > condition.getSize();
        if (hasNext) {
            content.remove(condition.getSize());
        }

        String nextCursor = null;
        if (hasNext && !content.isEmpty()) {
            nextCursor = content.get(content.size() - 1).getId().toString();
        }

        return new PageResponse<>(content, nextCursor, hasNext);
    }

    @Transactional
    public ChannelInviteResponse invite(Long inviterId, ChannelInviteRequest request) {
        Long channelId = request.getChannelId();
        Long inviteeId = request.getInviteeId();

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        User inviter = userRepository.findById(inviterId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        if (!channelMemberRepository.existsByChannelIdAndUserId(channelId, inviterId)) {
            throw new BusinessException(ErrorCode.NOT_ROOM_MEMBER);
        }

        User invitee = userRepository.findById(inviteeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        if (channelMemberRepository.existsByChannelIdAndUserId(channelId, inviteeId)) {
            throw new BusinessException(ErrorCode.ALREADY_JOINED_ROOM);
        }

        ChannelInvite channelInvite = new ChannelInvite(inviter, invitee, channel);
        this.channelInviteRepository.save(channelInvite);
        invitee.incrementChannelInviteCount();

        return new ChannelInviteResponse(
                channelInvite,
                new UserInfoResponse(inviter),
                new UserInfoResponse(invitee)
        );
    }

    @Transactional
    public void accept(Long invitationId, Long userId) {
        ChannelInvite invitation = channelInviteRepository.findById(invitationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVITATION_NOT_FOUND));

        User invitee = invitation.getInvitee();

        if (!invitee.getId().equals(userId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        Channel channel = invitation.getChannel();

        if (channelMemberRepository.existsByChannelIdAndUserId(channel.getId(), userId)) {
            throw new BusinessException(ErrorCode.ALREADY_JOINED_ROOM);
        }

        ChannelMember channelMember = new ChannelMember(channel, invitee);
        channelMemberRepository.save(channelMember);

        channel.incrementMemberCount();
        invitee.decrementChannelInviteCount();
        invitation.accept();

        chatService.broadcastMemberUpdate(channel.getId(), channel.getMemberCount());
    }

    @Transactional
    public void reject(Long invitationId, Long userId) {
        ChannelInvite invitation = channelInviteRepository.findById(invitationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVITATION_NOT_FOUND));

        User invitee = invitation.getInvitee();

        if (!invitee.getId().equals(userId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        if (channelMemberRepository.existsByChannelIdAndUserId(invitation.getChannel().getId(), userId)) {
            throw new BusinessException(ErrorCode.ALREADY_JOINED_ROOM);
        }

        invitation.decline();
        invitee.decrementChannelInviteCount();
    }
}
