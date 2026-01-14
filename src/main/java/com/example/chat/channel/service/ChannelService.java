package com.example.chat.channel.service;

import com.example.chat.channel.domain.Channel;
import com.example.chat.channel.domain.ChannelMember;
import com.example.chat.channel.dto.ChannelCreateRequest;
import com.example.chat.channel.dto.ChannelMemberResponse;
import com.example.chat.channel.dto.ChannelSummaryProjection;
import com.example.chat.channel.dto.ChannelSummaryResponse;
import com.example.chat.channel.repository.ChannelMemberRepository;
import com.example.chat.channel.repository.ChannelRepository;
import com.example.chat.channel.util.ChannelMemberCursorMapper;
import com.example.chat.global.dto.PageResponse;
import com.example.chat.global.error.BusinessException;
import com.example.chat.global.error.ErrorCode;
import com.example.chat.message.repository.MessageRepository;
import com.example.chat.user.domain.User;
import com.example.chat.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChannelService {

    private final ChannelRepository channelRepository;

    private final ChannelMemberRepository channelMemberRepository;

    private final MessageRepository messageRepository;

    private final UserRepository userRepository;

    @Transactional
    public ChannelSummaryResponse create(Long userId, ChannelCreateRequest channelCreateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        Channel channel = channelRepository.save(Channel.from(channelCreateRequest));

        ChannelMember channelMember = new ChannelMember(channel, user);
        channelMemberRepository.save(channelMember);
        channel.incrementMemberCount();

        return ChannelSummaryResponse.from(channel, 0);
    }

    @Transactional(readOnly = true)
    public PageResponse<ChannelSummaryResponse> getChannels(Long userId, String cursor, int limit) {
        Slice<ChannelSummaryProjection> projectionSlice = fetchProjections(userId, cursor, limit);
        List<ChannelSummaryProjection> projections = projectionSlice.getContent();

        if (projections.isEmpty()) {
            return new PageResponse<>(Collections.emptyList(), null, false);
        }

        Map<Long, Integer> unreadCounts = resolveUnreadCounts(userId, projections);

        List<ChannelSummaryResponse> responses = createResponses(projections, unreadCounts);
        String nextCursor = getNextCursor(projectionSlice, responses);
        return new PageResponse<>(responses, nextCursor, projectionSlice.hasNext());
    }


    @Transactional
    public PageResponse<ChannelMemberResponse> getChannelMembers(Long channelId, String cursor, int limit) {
        ChannelMemberCursorMapper.CursorData cursorData = ChannelMemberCursorMapper.fromCursor(cursor);

        Slice<ChannelMemberResponse> memberSlice = channelMemberRepository.findMembersByCursor(
                channelId,
                cursorData.nickname(),
                cursorData.id(),
                PageRequest.of(0, limit)
        );

        String nextCursor = null;
        if (memberSlice.hasNext()) {
            ChannelMemberResponse lastMember = memberSlice.getContent().get(memberSlice.getContent().size() - 1);
            nextCursor = ChannelMemberCursorMapper.toCursor(lastMember.getNickname(), lastMember.getUserId());
        }

        return new PageResponse<>(
                memberSlice.getContent(),
                nextCursor,
                memberSlice.hasNext()
        );
    }

    @Transactional
    public void updateChannelPreview(Long channelId, Long lastMessageId, String content, Instant createdAt) {
        String truncatedContent = content.length() > 100 ? content.substring(0, 97) + "..." : content;
        channelRepository.updateLastMessage(
                channelId,
                lastMessageId,
                truncatedContent,
                createdAt
        );
        channelMemberRepository.updateLastMessageId(channelId, lastMessageId);
    }

    @Async
    @Transactional
    public void markAsRead(Long channelId, Long userId, Long requestMessageId) {
        channelMemberRepository.updateLastReadMessageIdSafe(channelId, userId, requestMessageId);

        // Redis 갱신 로직 제거 (DB 조회로 변경되었으므로 불필요)
    }

    @Transactional(readOnly = true)
    public void validateUserMembership(Long channelId, Long userId) {
        boolean isMember = channelMemberRepository.existsByChannelIdAndUserId(channelId, userId);
        if (!isMember) {
            throw new BusinessException(ErrorCode.NOT_ROOM_MEMBER);
        }
    }

// ================== Private Helper Methods ==================

    private Slice<ChannelSummaryProjection> fetchProjections(Long userId, String cursor, int limit) {
        Long cursorId = cursor != null ? Long.valueOf(cursor) : null;
        return channelRepository.findChannelProjectionsByUserId(userId, cursorId, PageRequest.of(0, limit));
    }

    /**
     * 핵심 로직: DB Bulk 조회 -> 최종 Map 반환 (Redis 제거됨)
     */
    private Map<Long, Integer> resolveUnreadCounts(Long userId, List<ChannelSummaryProjection> projections) {
        Map<Long, Long> channelReadMap = new HashMap<>();

        for (ChannelSummaryProjection proj : projections) {
            channelReadMap.put(proj.getChannelId(), proj.getMyLastReadMessageId());
        }

        if (channelReadMap.isEmpty()) {
            return Collections.emptyMap();
        }

        // DB에서 일괄 조회
        return messageRepository.countUnreadMessagesBatch(channelReadMap);
    }

    private List<ChannelSummaryResponse> createResponses(List<ChannelSummaryProjection> projections, Map<Long, Integer> unreadCounts) {
        return projections.stream()
                .map(proj -> ChannelSummaryResponse.from(
                        proj,
                        unreadCounts.getOrDefault(proj.getChannelId(), 0) // 안전하게 0 처리
                ))
                .toList();
    }

    private String getNextCursor(Slice<ChannelSummaryProjection> slice, List<ChannelSummaryResponse> responses) {
        if (slice.hasNext() && !responses.isEmpty()) {
            return responses.get(responses.size() - 1).getLastMessageId().toString();
        }
        return null;
    }
}
