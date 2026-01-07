package com.example.chat.channel.service;

import com.example.chat.channel.domain.Channel;
import com.example.chat.channel.domain.ChannelMember;
import com.example.chat.channel.dto.*;
import com.example.chat.channel.repository.ChannelMemberRepository;
import com.example.chat.channel.repository.ChannelRepository;
import com.example.chat.channel.util.ChannelMemberCursorMapper;
import com.example.chat.global.dto.PageResponse;
import com.example.chat.global.error.BusinessException;
import com.example.chat.global.error.ErrorCode;
import com.example.chat.global.redis.RedisService;
import com.example.chat.invitation.repository.ChannelInviteRepository;
import com.example.chat.message.repository.MessageRepository;
import com.example.chat.user.domain.User;
import com.example.chat.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class  ChannelService {

    private final ChannelRepository channelRepository;
    private final ChannelMemberRepository channelMemberRepository;
    private final ChannelInviteRepository channelInviteRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    private final RedisService redisService;

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

        // 2. [데이터 보정] 안 읽은 메시지 수 계산 (Redis + DB Hybrid Logic)
        Map<Long, Integer> unreadCounts = resolveUnreadCounts(userId, projections);

        // 3. [데이터 가공] 응답 DTO 변환
        List<ChannelSummaryResponse> responses = createResponses(projections, unreadCounts);

        // 4. [커서 계산] 다음 페이지 커서 추출
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

        String key = "unread:" + userId + ":" + channelId;
        redisService.setValues(key, "0", Duration.ofHours(1));
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
     * 핵심 로직: Redis 캐시 조회 -> Cache Miss 수집 -> DB Bulk 조회 -> Redis 갱신 -> 최종 Map 반환
     */
    private Map<Long, Integer> resolveUnreadCounts(Long userId, List<ChannelSummaryProjection> projections) {
        List<String> redisKeys = projections.stream()
                .map(p -> generateUnreadKey(userId, p.getChannelId()))
                .toList();

        List<String> redisValues = redisService.getValuesList(redisKeys);

        Map<Long, Integer> resultMap = new HashMap<>();
        Map<Long, Long> cacheMissChannels = new HashMap<>();

        // 1. Redis 결과 분류
        for (int i = 0; i < projections.size(); i++) {
            ChannelSummaryProjection proj = projections.get(i);
            String redisVal = redisValues.get(i);

            if (redisVal != null) {
                resultMap.put(proj.getChannelId(), Integer.parseInt(redisVal));
            } else {
                cacheMissChannels.put(proj.getChannelId(), proj.getMyLastReadMessageId());
            }
        }

        // 2. Cache Miss 처리 (DB Bulk Query + Redis Repair)
        if (!cacheMissChannels.isEmpty()) {
            Map<Long, Integer> dbCounts = messageRepository.countUnreadMessagesBatch(cacheMissChannels);

            for (Map.Entry<Long, Long> entry : cacheMissChannels.entrySet()) {
                Long channelId = entry.getKey();
                int count = dbCounts.getOrDefault(channelId, 0);

                // 결과 맵에 추가
                resultMap.put(channelId, count);

                // Redis 복구 (Async로 처리하면 더 좋음)
                redisService.setValues(
                        generateUnreadKey(userId, channelId),
                        String.valueOf(count),
                        Duration.ofHours(1)
                );
            }
        }

        return resultMap;
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

    private String generateUnreadKey(Long userId, Long channelId) {
        return "unread:" + userId + ":" + channelId;
    }
}
