package com.example.chat.message.service;

import com.example.chat.channel.repository.ChannelMemberRepository;
import com.example.chat.channel.service.ChannelService;
import com.example.chat.global.error.BusinessException;
import com.example.chat.global.error.ErrorCode;
import com.example.chat.message.domain.Message;
import com.example.chat.message.dto.MessageCreateRequest;
import com.example.chat.message.dto.MessageEventPayload;
import com.example.chat.message.dto.MessageLoadResponse;
import com.example.chat.message.dto.MessageResponse;
import com.example.chat.message.dto.MessageView;
import com.example.chat.message.mapper.MessageMapper;
import com.example.chat.message.repository.MessageRepository;
import com.example.chat.user.domain.User;
import com.example.chat.user.dto.UserInfoProjection;
import com.example.chat.user.dto.UserInfoResponse;
import com.example.chat.user.mapper.UserMapper;
import com.example.chat.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChannelService channelService;
    private final ChannelMemberRepository channelMemberRepository;
    private final UserRepository userRepository;
    private final MessageMapper messageMapper;
    private final UserMapper userMapper;

    @Transactional
    public MessageEventPayload createAndSaveMessage(Long channelId, Long authorId, MessageCreateRequest sendMessageDto) {
        boolean isMember = channelMemberRepository.existsByChannelIdAndUserId(channelId, authorId);
        if (!isMember) {
            throw new BusinessException(ErrorCode.NOT_ROOM_MEMBER);
        }

        Message message = Message.create(channelId, authorId, sendMessageDto.getContent());
        message = messageRepository.save(message);

        UserInfoProjection userInfoProjection = userRepository.findUserInfoByIdCustom(authorId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        MessageResponse messageResponse = messageMapper.toResponse(message);
        UserInfoResponse userInfoResponse = userMapper.toResponse(userInfoProjection);
        return messageMapper.toEventPayload(messageResponse, userInfoResponse);
    }

    @Transactional
    public MessageLoadResponse getMessages(Long channelId, Long userId, Long cursor, int size) {
        channelService.validateUserMembership(channelId, userId);

        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"));
        Long effectiveCursor = (cursor == null) ? Long.MAX_VALUE : cursor;
        Slice<MessageView> messageSlice = messageRepository.findByChannelIdAndIdLessThan(
                channelId,
                effectiveCursor,
                pageable
        );

        List<MessageView> messageViews = messageSlice.getContent();
        List<MessageResponse> messages = messageMapper.toResponseList(messageViews);
        boolean hasNext = messageSlice.hasNext();
        String nextCursor = (hasNext && !messages.isEmpty()) ? messages.get(messages.size() - 1).getMessageId() : null;

        Set<String> authorIds = messages.stream()
                .map(MessageResponse::getAuthorId)
                .collect(Collectors.toSet());
        List<User> users = userRepository.findByIdIn(authorIds);
        Map<String, UserInfoResponse> userMap = users.stream()
                .collect(Collectors.toMap(
                        user -> String.valueOf(user.getId()),
                        UserInfoResponse::new
                ));

        return new MessageLoadResponse(
                messages,
                userMap, nextCursor,
                hasNext
        );
    }
}
