package com.example.chat.channel.controller;

import com.example.chat.channel.dto.*;
import com.example.chat.channel.service.ChannelService;
import com.example.chat.global.dto.PageResponse;
import com.example.chat.global.security.user.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ChannelApiController {

    private final ChannelService channelService;

    @GetMapping("/channels")
    public ResponseEntity<PageResponse<ChannelSummaryResponse>> getChannels(
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        PageResponse<ChannelSummaryResponse> response = channelService.getChannels(
                userDetails.getId(),
                cursor,
                size
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/channels/{channelId}/members")
    public ResponseEntity<PageResponse<ChannelMemberResponse>> getChannelMembers(
            @PathVariable Long channelId,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        PageResponse<ChannelMemberResponse> response = channelService.getChannelMembers(
                channelId,
                cursor,
                size
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/channels")
    public ResponseEntity<ChannelSummaryResponse> createChannel(
            @RequestBody ChannelCreateRequest channelCreateRequest,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        ChannelSummaryResponse channelSummaryResponse = channelService.create(userDetails.getId(), channelCreateRequest);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(channelSummaryResponse.getChannelId())
                .toUri();
        return ResponseEntity.created(location).body(channelSummaryResponse);
    }
}
