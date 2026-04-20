package com.example.chat.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfoProjection {
    private String id;
    private String username;
    private String nickname;
    private String profileImageUrl;
    private String profileIconColor;
}
