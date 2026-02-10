package com.example.chat.user.dto;

import com.example.chat.user.domain.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;

    private String username;

    private String nickname;

    private String profileImageUrl;

    private String profileIconColor;

    public UserInfoResponse(User user) {
        this.userId = user.getId();
        this.username = user.getUsername();
        this.nickname = user.getNickname();
        this.profileImageUrl = user.getProfileImageUrl();
        this.profileIconColor = user.getProfileIconColor();
    }

    public UserInfoResponse(UserInfoProjection projection) {
        this.userId = Long.valueOf(projection.getId());
        this.username = projection.getUsername();
        this.nickname = projection.getNickname();
        this.profileImageUrl = projection.getProfileImageUrl();
        this.profileIconColor = projection.getProfileIconColor();
    }
}
