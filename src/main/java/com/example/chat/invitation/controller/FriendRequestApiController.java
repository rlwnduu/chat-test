package com.example.chat.invitation.controller;

import com.example.chat.global.dto.PageResponse;
import com.example.chat.global.security.user.CustomUserDetails;
import com.example.chat.invitation.dto.CreateFriendRequest;
import com.example.chat.invitation.dto.FriendRequestResponse;
import com.example.chat.invitation.dto.InviteSearchCondition;
import com.example.chat.invitation.service.FriendRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FriendRequestApiController {

    private final FriendRequestService friendRequestService;

    @GetMapping("/users/@me/friend-requests")
    public ResponseEntity<PageResponse<FriendRequestResponse>> getMyChannelInvitations(
            InviteSearchCondition condition,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        condition.setUserId(userDetails.getId());
        PageResponse<FriendRequestResponse> response = friendRequestService.getFriendRequests(condition);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/friend-requests")
    public ResponseEntity<Void> sendFriendRequest(
            @RequestBody CreateFriendRequest createFriendRequest,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long inviterId = userDetails.getId();
        friendRequestService.request(inviterId, createFriendRequest);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/friend-requests/{requestId}/accept")
    public ResponseEntity<Void> acceptFriendRequest(
            @PathVariable Long requestId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long inviteeId = userDetails.getId();
        friendRequestService.accept(requestId, inviteeId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/friend-requests/{requestId}/reject")
    public ResponseEntity<Void> rejectFriendRequest(
            @PathVariable Long requestId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long inviteeId = userDetails.getId();
        friendRequestService.reject(requestId, inviteeId);
        return ResponseEntity.noContent().build();
    }
}
