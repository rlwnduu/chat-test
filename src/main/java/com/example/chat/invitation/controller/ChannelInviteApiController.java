package com.example.chat.invitation.controller;

import com.example.chat.global.dto.PageResponse;
import com.example.chat.global.security.user.CustomUserDetails;
import com.example.chat.invitation.dto.ChannelInviteRequest;
import com.example.chat.invitation.dto.ChannelInviteResponse;
import com.example.chat.invitation.service.ChannelInviteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ChannelInviteApiController {

    private final ChannelInviteService channelInviteService;

    @GetMapping("/users/@me/channel-invites")
    public ResponseEntity<PageResponse<ChannelInviteResponse>> getMyChannelInvitations(
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getId();
        PageResponse<ChannelInviteResponse> response
                = channelInviteService.getChannelInvitations(userId, cursor, size);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/channel-invites")
    public ResponseEntity<Void> inviteToChannel(
            @RequestBody ChannelInviteRequest requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        ChannelInviteResponse channelInviteResponse = channelInviteService.invite(
                userDetails.getId(),
                requestDto
        );
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/channel-invites/{invitationId}/accept")
    public ResponseEntity<Void> acceptInvitation(
            @PathVariable Long invitationId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        channelInviteService.accept(invitationId, userDetails.getId());
        return ResponseEntity.noContent().build();

    }

    @PostMapping("/channel-invites/{invitationId}/reject")
    public ResponseEntity<Void> reject(
            @PathVariable Long invitationId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        channelInviteService.reject(invitationId, userDetails.getId());
        return ResponseEntity.noContent().build();
    }
}
