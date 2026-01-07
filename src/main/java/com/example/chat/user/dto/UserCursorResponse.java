package com.example.chat.user.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class UserCursorResponse {

    @Getter
    private List<UserInfoProjection> users;

    @Getter
    private String cursor;

    @Getter
    private boolean hasNext;

    public UserCursorResponse(List<UserInfoProjection> users, String cursor, boolean hasNext) {
        this.users = users;
        this.cursor = cursor;
        this.hasNext = hasNext;
    }
}
