package com.example.chat.invitation.service;

import com.example.chat.channel.domain.Channel;
import com.example.chat.channel.domain.ChannelMember;
import com.example.chat.channel.repository.ChannelMemberRepository;
import com.example.chat.channel.repository.ChannelRepository;
import com.example.chat.global.dto.PageResponse;
import com.example.chat.global.error.BusinessException;
import com.example.chat.global.error.ErrorCode;
import com.example.chat.invitation.domain.ChannelInvite;
import com.example.chat.invitation.dto.ChannelInviteRequest;
import com.example.chat.invitation.dto.ChannelInviteResponse;
import com.example.chat.invitation.repository.ChannelInviteRepository;
import com.example.chat.message.service.ChatService;
import com.example.chat.user.domain.User;
import com.example.chat.user.dto.UserInfoResponse;
import com.example.chat.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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

    @Transactional(readOnly = true) // 조회의 경우 readOnly 권장
    public PageResponse<ChannelInviteResponse> getChannelInvitations(Long inviteeId, String cursor, int size) {
        Long cursorId = (cursor == null) ? null : Long.parseLong(cursor);
        Pageable pageable = PageRequest.of(0, size);

        Slice<ChannelInviteResponse> slice = channelInviteRepository
                .findInvitationsByInviteeIdWithCursor(inviteeId, cursorId, pageable);

        List<ChannelInviteResponse> content = slice.getContent();
        boolean hasNext = slice.hasNext();
        String nextCursor = hasNext ? content.get(content.size() - 1).getId().toString() : null;

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

        // 초대자가 채널 멤버인지 확인
        if (!channelMemberRepository.existsByChannelIdAndUserId(channelId, inviterId)) {
            throw new BusinessException(ErrorCode.NOT_ROOM_MEMBER);
        }

        User invitee = userRepository.findById(inviteeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        // 초대받는 사람이 이미 멤버인지 확인
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
                // ErrorCode에 INVITATION_NOT_FOUND 추가 권장 (혹은 C006 INVALID_INVITATION_CODE 사용)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVITATION_NOT_FOUND));

        User invitee = invitation.getInvitee();

        // 당사자 확인
        if (!invitee.getId().equals(userId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED); // 혹은 "권한 없음"
        }

        Channel channel = invitation.getChannel();

        // 이미 가입된 상태인지 이중 체크
        if (channelMemberRepository.existsByChannelIdAndUserId(channel.getId(), userId)) {
            throw new BusinessException(ErrorCode.ALREADY_JOINED_ROOM);
        }

        ChannelMember channelMember = new ChannelMember(channel, invitee);
        channelMemberRepository.save(channelMember);

        channel.incrementMemberCount();
        invitee.decrementChannelInviteCount();
        invitation.accept(); // 상태 변경 (ACCEPTED)

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

        invitation.decline(); // 상태 변경 (REJECTED)
        invitee.decrementChannelInviteCount();
    }
}
