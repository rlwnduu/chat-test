package com.example.chat.user.dto;

import com.example.chat.user.domain.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
public class UserInfoResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;

    private String username;

    private String nickname;

    private String profileImageUrl;

    private String profileIconColor;

    public UserInfoResponse() {
    }

    public UserInfoResponse(User user) {
        this.userId = user.getId();
        this.username = user.getUsername();
        this.nickname = user.getNickname();
        this.profileImageUrl = user.getProfileImageUrl();
        this.profileIconColor = user.getProfileIconColor();
    }

    public UserInfoResponse(Long userId, String username, String nickname, String profileImageUrl, String profileIconColor) {
        this.userId = userId;
        this.username = username;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.profileIconColor = profileIconColor;
    }
}
