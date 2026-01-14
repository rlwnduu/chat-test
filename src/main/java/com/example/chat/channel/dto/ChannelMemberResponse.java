package com.example.chat.channel.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
public class ChannelMemberResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;

    private String username;

    private String nickname;

    private String profileImageUrl;

    private String profileIconColor;

    public ChannelMemberResponse() {
    }

    public ChannelMemberResponse(Long userId, String username, String nickname, String profileImageUrl, String profileIconColor) {
        this.userId = userId;
        this.username = username;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.profileIconColor = profileIconColor;
    }
}
