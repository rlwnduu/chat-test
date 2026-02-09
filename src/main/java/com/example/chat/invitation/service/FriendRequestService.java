package com.example.chat.invitation.service;

import com.example.chat.global.dto.PageResponse;
import com.example.chat.global.error.BusinessException;
import com.example.chat.global.error.ErrorCode;
import com.example.chat.invitation.domain.FriendRequest;
import com.example.chat.invitation.domain.RequestStatus;
import com.example.chat.invitation.dto.CreateFriendRequest;
import com.example.chat.invitation.dto.FriendRequestResponse;
import com.example.chat.invitation.repository.FriendRequestRepository;
import com.example.chat.user.domain.User;
import com.example.chat.user.domain.UserFriend;
import com.example.chat.user.repository.UserFriendRepository;
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
public class FriendRequestService {

    private final FriendRequestRepository friendRequestRepository;
    private final UserRepository userRepository;
    private final UserFriendRepository userFriendRepository;

    @Transactional(readOnly = true)
    public PageResponse<FriendRequestResponse> getFriendRequests(Long inviteeId, String cursor, int size) {
        Long cursorId = (cursor == null) ? null : Long.parseLong(cursor);
        Pageable pageable = PageRequest.of(0, size);

        Slice<FriendRequestResponse> slice = friendRequestRepository.findRequestsByInviteeIdAndStatusWithCursor(
                inviteeId,
                RequestStatus.PENDING,
                cursorId,
                pageable
        );

        List<FriendRequestResponse> friendRequests = slice.getContent();
        boolean hasNext = slice.hasNext();
        String nextCursor = hasNext ? friendRequests.get(friendRequests.size() - 1).getId().toString() : null;

        return new PageResponse<>(friendRequests, nextCursor, hasNext);
    }

    @Transactional
    public void request(Long inviterId, CreateFriendRequest request) {
        String targetUsername = request.getUsername();

        User inviter = userRepository.findById(inviterId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        User invitee = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        if (inviter.getId().equals(invitee.getId())) {
            throw new BusinessException(ErrorCode.SELF_FRIEND_REQUEST);
        }

        if (userFriendRepository.existsByUserAAndUserB(inviter, invitee)) {
            throw new BusinessException(ErrorCode.ALREADY_FRIEND);
        }

        if (friendRequestRepository.existsByInviterAndInviteeAndStatus(inviter, invitee, RequestStatus.PENDING)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        FriendRequest friendRequestEntity = new FriendRequest(inviter, invitee);
        friendRequestRepository.save(friendRequestEntity);
        invitee.incrementFriendRequestCount();
    }

    @Transactional
    public void accept(Long requestId, Long inviteeId) {
        FriendRequest friendRequest = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FRIEND_REQUEST_NOT_FOUND));

        if (friendRequest.getStatus() != RequestStatus.PENDING) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        User invitee = friendRequest.getInvitee();
        if (!invitee.getId().equals(inviteeId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        User inviter = friendRequest.getInviter();

        UserFriend userFriend = new UserFriend(inviter, invitee);
        userFriendRepository.save(userFriend);

        friendRequest.accept();

        inviter.incrementFriendCount();
        invitee.incrementFriendCount();
        invitee.decrementFriendRequestCount();
    }

    @Transactional
    public void reject(Long requestId, Long inviteeId) {
        FriendRequest friendRequest = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FRIEND_REQUEST_NOT_FOUND));

        if (friendRequest.getStatus() != RequestStatus.PENDING) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        User invitee = friendRequest.getInvitee();
        if (!invitee.getId().equals(inviteeId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        friendRequest.decline();
        invitee.decrementFriendRequestCount();
    }
}
