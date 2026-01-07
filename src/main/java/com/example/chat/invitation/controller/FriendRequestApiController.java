package com.example.chat.invitation.controller;

import com.example.chat.global.dto.PageResponse;
import com.example.chat.invitation.dto.*;
import com.example.chat.invitation.service.FriendRequestService;
import com.example.chat.global.security.user.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FriendRequestApiController {

    private final FriendRequestService friendRequestService;

    @GetMapping("/users/@me/friend-requests")
    public ResponseEntity<PageResponse<FriendRequestResponse>> getMyChannelInvitations(
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long inviteeId = userDetails.getId();
        PageResponse<FriendRequestResponse> response = friendRequestService.getFriendRequests(inviteeId, cursor, size);
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
