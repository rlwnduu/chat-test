package com.example.chat.user.dto;

import lombok.Getter;
import lombok.ToString;

@ToString
public class UserCreateRequest {

    @Getter
    private String loginId;

    @Getter
    private String password;

    public UserCreateRequest() {
    }

    public UserCreateRequest(String loginId, String password) {
        this.loginId = loginId;
        this.password = password;
    }
}
