package com.example.chat.channel.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChannelMemberProjection {
    private Long userId;
    private String username;
    private String nickname;
    private String profileImageUrl;
    private String profileIconColor;
}
