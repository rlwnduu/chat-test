package com.example.chat.invitation.service;

import com.example.chat.channel.service.ChannelService;
import com.example.chat.global.dto.PageResponse;
import com.example.chat.global.error.BusinessException;
import com.example.chat.global.error.ErrorCode;
import com.example.chat.invitation.domain.FriendRequest;
import com.example.chat.invitation.domain.RequestStatus;
import com.example.chat.invitation.dto.CreateFriendRequest;
import com.example.chat.invitation.dto.FriendRequestCursorResponse;
import com.example.chat.invitation.dto.FriendRequestResponse;
import com.example.chat.invitation.repository.FriendRequestRepository;
import com.example.chat.user.domain.User;
import com.example.chat.user.domain.UserFriend;
import com.example.chat.user.repository.UserFriendRepository;
import com.example.chat.user.repository.UserRepository;
import com.example.chat.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    // private final UserService userService; // 사용하지 않으면 제거
    // private final ChannelService channelService; // 사용하지 않으면 제거

    @Transactional(readOnly = true)
    public PageResponse<FriendRequestResponse> getFriendRequests(Long inviteeId, String cursor, int size) {
        Long cursorId = (cursor == null) ? null : Long.parseLong(cursor); // NumberFormatException은 Global에서 처리됨
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

        // [검증 1] 자신에게 친구 요청 불가 (R005)
        if (inviter.getId().equals(invitee.getId())) {
            throw new BusinessException(ErrorCode.SELF_FRIEND_REQUEST);
        }

        // [검증 2] 이미 친구인지 확인 (R001)
        if (userFriendRepository.existsByUserAAndUserB(inviter, invitee)) {
            throw new BusinessException(ErrorCode.ALREADY_FRIEND);
        }

        // [검증 3] 이미 보낸 대기중인 요청이 있는지 확인 (중복 요청 방지)
        // (ErrorCode에 DUPLICATE_REQUEST 같은게 없다면 ALREADY_FRIEND나 INVALID_INPUT 등으로 대체)
        if (friendRequestRepository.existsByInviterAndInviteeAndStatus(inviter, invitee, RequestStatus.PENDING)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE); // 혹은 "이미 요청을 보냈습니다"
        }

        FriendRequest friendRequestEntity = new FriendRequest(inviter, invitee);
        friendRequestRepository.save(friendRequestEntity);
        invitee.incrementFriendRequestCount();
    }

    @Transactional
    public void accept(Long requestId, Long inviteeId) {
        FriendRequest friendRequest = friendRequestRepository.findById(requestId)
                // [수정] R002: 존재하지 않는 친구 요청
                .orElseThrow(() -> new BusinessException(ErrorCode.FRIEND_REQUEST_NOT_FOUND));

        // [수정] 상태 검사 -> 비즈니스 예외 처리
        if (friendRequest.getStatus() != RequestStatus.PENDING) {
            // 이미 수락/거절된 요청임
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        User invitee = friendRequest.getInvitee();
        // [수정] 권한 검사 -> A005: 접근 권한 없음
        if (!invitee.getId().equals(inviteeId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        User inviter = friendRequest.getInviter();

        // 양방향 친구 관계 저장 (보통 친구는 양방향이므로 A->B, B->A 둘 다 저장하거나 로직에 따라 하나만 저장)
        UserFriend userFriend = new UserFriend(inviter, invitee);
        userFriendRepository.save(userFriend);

        // 반대 방향도 저장해야 한다면 여기서 추가 (UserFriend 구조에 따라 다름)
        // userFriendRepository.save(new UserFriend(invitee, inviter));

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
